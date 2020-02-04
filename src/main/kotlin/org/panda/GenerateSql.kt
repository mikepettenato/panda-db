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

    private val defaultDbms = "pgsql"

    @Input
    var dbms = defaultDbms

    /**
     * should the changelog table be created
     */
    @Input
    var createChangelog = false

    /**
     * set the location of the sql file for the changelog file
     * the default one packaged is for postgres
     */
    @Input
    var changeLogScript: String? = null

    open internal fun getChangelogSql(): String {

        // We only support a default changeLogScript for postgres (pgsql)
        // if dbms != pgsql, changeLogScript == null and createChangeLog == true then throw an exception
        if (dbms != defaultDbms && changeLogScript == null) {
            throw PandaDbException("changeLogScript is not set.  No changeLogScript for ${dbms} database")
        }

        var sql = PandaDbPlugin::class.java.getResource("/create_changelog_table.sql").readText()

        val changeLogFile = File(changeLogScript)

        if (changeLogScript != null) {
            if (!changeLogFile.exists() || !changeLogFile.isFile()) {
                throw PandaDbException("changeLogFile set does not exist or is not a file")
            }

            sql = changeLogFile.readText()
        }

        return sql
    }

    private fun createChangelogTable() {

        val sql = getChangelogSql()

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
