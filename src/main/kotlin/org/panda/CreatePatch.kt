package org.panda

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import java.util.*

open class CreatePatch: DefaultTask() {

    @Input
    var desc: String? = null

    @Input
    var dir: String = "patch"

    @TaskAction
    fun createPatch() {
        val out = services.get(StyledTextOutputFactory::class.java).create("createPatch")
        if (desc == null) {
            out.withStyle(StyledTextOutput.Style.Failure).println("You must pass a small description to this task")
            out.withStyle(StyledTextOutput.Style.Failure).println("Below is a sample run:")
            out.withStyle(StyledTextOutput.Style.Failure).println("\t./gradlew dbmanager:create_patch -Pdesc=create-test-table")
            return
        }

        val f = File(dir)
        if(!f.exists()) {
            f.mkdirs()
        }

        val desc = desc!!.replace(" ", "-")
        val timeMillis = Date().time
        val uuid = UUID.randomUUID()
        val rstr = uuid.toString().replace("-", "")
        val fileName = "${timeMillis}-${rstr}-${desc}.sql"
        val path = "${project.projectDir}/${dir}"
        File(path, fileName).writeText("""--- Put the ddl or dml directly below these comments
--- Put the undo ddl/dml below the @UNDO comment

---//@UNDO
 
"""
        )

        out.withStyle(StyledTextOutput.Style.Success).println("created the following patch file: ${path}/${fileName}")

    }
}
