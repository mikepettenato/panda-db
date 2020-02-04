package org.panda

import com.dbdeploy.DbDeploy
import org.gradle.internal.impldep.junit.framework.Assert
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.io.File

class GenerateSqlTest {

    val project = ProjectBuilder.builder().build()

    lateinit var dbDeploy: DbDeploy

    lateinit var task: GenerateSql

    lateinit var spy: GenerateSql

    @BeforeEach
    fun beforeEach() {

        dbDeploy = object: DbDeploy() {
            override fun go() {}
        }

        task = project.tasks.create("gerenate-sql-test", GenerateSql::class.java)

        spy = Mockito.spy(task)

        Mockito.doReturn(dbDeploy).`when`(spy).createDbDeploy()
    }

    @Test
    fun testGenerateSql() {

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

    @Test
    fun `changeLogFile is supplied if dbms is not pgsql`() {

        spy.dbms = "mysql"

        spy.createChangelog = true

        Assertions.assertThrows(PandaDbException::class.java, {

            spy.generateSql()
        })
    }
}
