package org.panda

import org.gradle.internal.Actions.doNothing
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File

class CreatePatchTest {

    val project = ProjectBuilder.builder().build()


    /**
     * Make sure that a patch file will not be created without a description
     */
    @Test
    fun testCreatePatch() {

        val task = project.tasks.create("create-patch-test", CreatePatch::class.java)

        Assertions.assertTrue(task is CreatePatch)

        Assertions.assertThrows(PandaDbException::class.java, Executable {

            task!!.createPatch()
        })
    }


    /**
     * Test to make sure that the patch files are acceptably random
     * even if the patch file is made at the exact correct millisecond
     * and with the same description.
     */
    @Test
    fun testPatchFileCreation() {

        val task = project.tasks.create("create-patch-test", CreatePatch::class.java)

        task.desc = "test"

        Assertions.assertTrue(task.patchFileName() != task.patchFileName())
    }

    /**
     * ensure that if the task is configured correctly
     * that the writePatchFile function gets called.
     */
    @Test
    fun testWritePatchFileGetsCalled() {

        val createPatch = Mockito.mock( CreatePatch::class.java)

        createPatch.desc = "This is a test"

        createPatch.projectDir = File("./")

        createPatch.dir = "patch"

        Mockito.`when`(createPatch.ensurePatchDirExists()).thenReturn(true)

        Mockito.`when`(createPatch.patchFileName()).thenReturn("test-file.sql")

        Mockito.`when`(createPatch.printFailureLine(ArgumentMatchers.anyString())).thenReturn(null)

        Mockito.`when`(createPatch.printSuccessLine(ArgumentMatchers.anyString())).thenReturn(null)

        Mockito.doNothing().`when`(createPatch).writePatchFile(ArgumentMatchers.anyString())


        createPatch.createPatch()

        Mockito.verify(createPatch).writePatchFile(ArgumentMatchers.anyString())

    }




}
