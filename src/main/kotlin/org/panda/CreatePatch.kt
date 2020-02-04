package org.panda

import org.gradle.api.DefaultTask
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import java.util.*
import javax.imageio.spi.ServiceRegistry

open class CreatePatch: DefaultTask() {

    @Input
    var desc: String? = null

    @Input
    var dir: String = "patch"

    internal var out = services.get(StyledTextOutputFactory::class.java).create("createPatch")

    internal var projectDir = project.projectDir

    val charList = listOf<String>(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "a", "b", "c", "d", "e", "f", "g", "h","i", "j", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    val random = Random()

    open internal fun ensurePatchDirExists(): Boolean {

        val f = File(dir)
        if(!f.exists()) {
            return f.mkdirs()
        }

        return true
    }

    open internal fun printFailureLine(s: String): StyledTextOutput {
        return out.withStyle(StyledTextOutput.Style.Failure).println(s)
    }

    open internal fun printSuccessLine(s: String): StyledTextOutput {
        return out.withStyle(StyledTextOutput.Style.Success).println(s)
    }

    open internal fun writePatchFile(fileName: String): Unit {

        val file = File(path, fileName)

        file.writeText("""--- Put the ddl or dml directly below these comments
--- Put the undo ddl/dml below the @UNDO comment

---//@UNDO
 
"""
        )
    }

    private fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }

    open internal fun patchFileName(): String {
        val desc = desc!!.replace(" ", "-")
        val timeMillis = Date().time
        //val uuid = UUID.randomUUID()
        val uuid = charList[rand(0 , charList.size)] +
                charList[rand(0 , charList.size)] +
                charList[rand(0 , charList.size)]

        //val rstr = uuid.toString().replace("-", "")
        return "${timeMillis}-${uuid}-${desc}.sql"

    }

    @TaskAction
    fun createPatch() {

        if (desc == null) {
            printFailureLine("You must pass a small description to this task")
            printFailureLine("Below is a sample run:")
            printFailureLine("\t./gradlew dbmanager:create_patch -Pdesc=create-test-table")
            throw PandaDbException("desc property is not set")
        }

        ensurePatchDirExists()

        val path = "${projectDir}/${dir}"

        val fileName = patchFileName()


        writePatchFile(fileName)

        printSuccessLine("created the following patch file: ${path}/${fileName}")

    }
}
