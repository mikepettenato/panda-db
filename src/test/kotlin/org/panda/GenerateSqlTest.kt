package org.panda

import com.dbdeploy.DbDeploy
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.io.File

class GenerateSqlTest {

    val project = ProjectBuilder.builder().build()

    @Test
    fun testGenerateSql() {

        val dbDeploy = object: DbDeploy() {
            override fun go() {}
        }

        val task = project.tasks.create("gerenate-sql-test", GenerateSql::class.java)

        val spy = Mockito.spy(task)

        Mockito.doReturn(dbDeploy).`when`(spy).createDbDeploy()

        spy.generateSql()

        Assertions.assertEquals(task.dbms, dbDeploy.dbms)

        Assertions.assertEquals(task.url, dbDeploy.url)

        Assertions.assertEquals(task.driver, dbDeploy.driver)

        Assertions.assertEquals(File(task.outputFile), dbDeploy.outputfile)

        Assertions.assertEquals(File(task.undoOutputFile), dbDeploy.undoOutputfile)

        Assertions.assertEquals(File(task.patchDir), dbDeploy.scriptdirectory)

        Assertions.assertEquals(task.user, dbDeploy.userid)

        Assertions.assertEquals(task.password, dbDeploy.password)

    }
}
