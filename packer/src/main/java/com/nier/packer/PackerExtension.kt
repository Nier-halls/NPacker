package com.nier.packer

import com.android.build.gradle.api.BaseVariant
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.ActionConfiguration
import org.gradle.api.InvalidUserDataException
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.NamedDomainObjectContainerConfigureDelegate
import org.gradle.internal.Actions
import org.gradle.internal.metaobject.ConfigureDelegate
import org.gradle.util.ConfigureUtil

/**
 * Created by Nier
 * Date 2018/7/26
 */
open class PackerExtension() {

    var apkOutputDir: String? = null
    var apkNamePattern: String? = null
    var mappingOutputDir: String? = null
    val channelContainer = DefaultChannelContainer()

    fun channels(configuration: Action<DefaultChannelContainer>) {
        configuration.execute(this.channelContainer)
    }
}

open class Channel(val name: String) {
    var key = ""
    val fields = HashMap<String, Any?>()


    fun channelKey(_key: String) {
        key = _key
    }

    fun field(field: String, value: Any?) {
        fields[field] = value
    }

    override fun toString(): String {
        return ">>[name = ${name}, key = ${key}, fields = ${fields}]"
    }
}

open class DefaultChannelContainer() {
    val container = HashMap<String, Channel>()

    fun channel(name: String, configuration: Closure<Channel>) {
        val newChannel = Channel(name)
        configuration.delegate = newChannel
        configuration.resolveStrategy = Closure.DELEGATE_FIRST
        configuration.run()
        container[name] = newChannel
    }

    fun channel(map: HashMap<String, Closure<Channel>>) {
        map.forEach { entry: Map.Entry<String, Closure<Channel>> ->
            val name = entry.key
            val configuration = entry.value
            channel(name, configuration)
        }
    }

    override fun toString(): String {
        return container.toString()
    }
}