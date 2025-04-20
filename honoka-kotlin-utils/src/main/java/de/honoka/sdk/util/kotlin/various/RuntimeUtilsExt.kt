package de.honoka.sdk.util.kotlin.various

import de.honoka.sdk.util.various.RuntimeUtils

object RuntimeUtilsExt {

    fun exec(block: RuntimeUtils.Commands.() -> Unit): String = run {
        RuntimeUtils.exec(RuntimeUtils.Commands().apply(block))
    }
}
