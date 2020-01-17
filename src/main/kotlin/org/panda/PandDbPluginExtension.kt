package org.panda

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

open class PandDbPluginExtension {

    var driver = "org.postgresql.Driver"
    var url = "jdbc:postgresql://127.0.0.1/test"

}
