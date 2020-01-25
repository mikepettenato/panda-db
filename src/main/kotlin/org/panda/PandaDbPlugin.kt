package org.panda

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.sql.SQLException
import java.sql.Statement
import java.util.*

open class PandaDbPlugin: Plugin<Project> {
    override fun apply(project: Project) {

        project.tasks.create("generate-sql", GenerateSql::class.java)

        project.tasks.create("create-patch", CreatePatch::class.java)

    }

}
