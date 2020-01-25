package org.panda

import org.gradle.api.GradleException

class PandaDbException(
        override val message: String
): GradleException(message) {
}
