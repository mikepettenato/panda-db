package org.panda.gdbdeploy

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

open class GdbdeployPluginExtension {

    var driver = "org.postgresql.Driver"
    var url = "jdbc:postgresql://127.0.0.1/test"

}
