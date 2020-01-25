package org.panda

import com.dbdeploy.DbDeploy
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.sql.SQLException
import java.sql.Statement
import java.util.*

open class GenerateSql: DefaultTask() {

    @Input
    var driver = "org.postgresql.Driver"

    @Input
    var url = "jdbc:postgresql://127.0.0.1/test"

    @Input
    var user = "test"

    @Input
    var password = "test"

    @Input
    var patchDir = "patch"

    @Input
    var outputFile = "output.sql"

    @Input
    var undoOutputFile = "undo.sql"

    @Input
    var dbms = "pgsql"

    @Input
    var createChangelog = false

    private fun createChangelogTable() {
        val sql = PandaDbPlugin::class.java.getResource("/create_changelog_table.sql").readText()
        val driver = PandaDbPlugin::class.java.classLoader.loadClass(this.driver).newInstance() as java.sql.Driver
        val props = Properties()
        props.put("user", user)
        props.put("password", password)
        val con = driver.connect(this.url, props)
        var stmt: Statement? = null
        try {
            con.autoCommit = true
            stmt = con.createStatement()
            stmt.execute(sql)
            stmt.close()
        }catch (e: SQLException) {
            e.printStackTrace()
        }finally{
            if (stmt != null) {
                stmt.close()
            }
            if (con != null) {
                con.close()
            }
        }

    }

    open internal fun createDbDeploy(): DbDeploy {
        return DbDeploy()
    }

    @TaskAction
    fun generateSql() {
        val undo = File(undoOutputFile)
        // validate that the change log exists.
        // if it does not exist create it.
        if (createChangelog) {
            println("creating changelog")
            createChangelogTable()
        }
        val dbDeploy = createDbDeploy()
        dbDeploy.driver = driver
        dbDeploy.url = url
        dbDeploy.userid = user
        dbDeploy.password = password
        dbDeploy.scriptdirectory = File(patchDir)
        dbDeploy.outputfile = File(outputFile)
        dbDeploy.undoOutputfile= File(undoOutputFile)
        dbDeploy.dbms = dbms
        dbDeploy.go()
    }
}
