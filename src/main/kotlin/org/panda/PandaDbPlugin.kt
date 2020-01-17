package org.panda

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.sql.SQLException
import java.sql.Statement
import java.util.*

open class PandaDbPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val config = project.extensions.create("config", PandDbPluginExtension::class.java)

        project.tasks.create("testIt", GenerateSql::class.java)

        project.tasks.create("create-patch", CreatePatch::class.java)

        project.run {
            val initChangelog = task("init-changelog")
            initChangelog
                    .doLast({
                        val sql = PandaDbPlugin::class.java.getResource("/create_changelog_table.sql").readText()
                        val driver = PandaDbPlugin::class.java.classLoader.loadClass(config.driver).newInstance() as java.sql.Driver
                        val props = Properties()
                        props.put("user", "test")
                        props.put("password", "test")
                        val con = driver.connect(config.url, props)
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
                    })

            val genSql = task("generatesql")

            genSql.dependsOn(initChangelog)

            genSql.doLast({
                println("Hello generatesql")
            })
        }
    }

}
