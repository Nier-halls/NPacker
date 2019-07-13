package com.nier.packer

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.nier.packer.channel.Channel
import groovy.lang.Closure
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Nier
 * Date 2018/7/26
 */

open class PackerExtension {

    var apkOutputDir: String? = null //apk输出路径
    var apkNamePattern: String? = null //apk输出名字规则
    var mappingOutputDir: String? = null //mappingOutputDir文件路径
    var channelContainer = ChannelContainer() //渠道容器

    /**
     *  渠道配置方法, gradle脚本配置渠道信息的方法
     * npacker {
     *      channels {
     *
     *      }
     * }
     */
    fun channels(configuration: Action<ChannelContainer>) {
        //todo closure是什么类型的，为什么参数传递进去就会在参数上下文执行?
        configuration.execute(this.channelContainer)
    }

    internal fun clone(): PackerExtension {
        val cloned = PackerExtension()
        cloned.apkNamePattern = this.apkNamePattern
        cloned.apkOutputDir = this.apkOutputDir
        cloned.mappingOutputDir = this.mappingOutputDir
        cloned.channelContainer = this.channelContainer.clone()
        return cloned
    }

    /**
     * apk输出路径地址
     */
    internal fun getOutputDir(project: Project): String? {
        return apkOutputDir
                ?: defaultOutPutDir(project)
    }

    /**
     * apk名字匹配规则
     */
    internal fun buildApkName(channel: String, variant: BaseVariant, project: Project): String {

        val channelPattern = apkNamePattern
                ?: return defaultApkName(channel, variant)
        return SimpleTemplateEngine()
                .createTemplate(channelPattern)
                .make(templateMap(channel, variant, project))
                .toString() + ".apk"
    }

    /**
     * 匹配templateMap
     */
    private fun templateMap(channel: String, sourceVariant: BaseVariant, project: Project): HashMap<String, Any?> {
        return hashMapOf(
                "appName" to project.name,
                "projectName" to project.rootProject.name,
                "channel" to channel,
                "flavor" to sourceVariant.flavorName,
                "buildType" to sourceVariant.buildType.name,
                "versionName" to ((sourceVariant as ApplicationVariantImpl).versionName ?: "null"),
                "versionCode" to sourceVariant.versionCode,
                "appPkg" to sourceVariant.applicationId,
                "buildTime" to SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        )
    }

    /**
     * 默认输出地址
     */
    private fun defaultOutPutDir(project: Project): String = "${project.rootDir}${File.separator}npacker${File.separator}outputs${File.separator}apk"

    /**
     * 默认输出Apk名字
     */
    private fun defaultApkName(channel: String, variant: BaseVariant): String = "$channel${variant.name?.capitalize()}.apk"
}

open class ChannelContainer : HashMap<String, Channel> {

    constructor() : super()

    constructor(m: MutableMap<String, Channel>?) : super(m)

    fun channel(name: String, configuration: Closure<Channel>) {
        val newChannel = Channel(name)
        configuration.delegate = newChannel
        configuration.resolveStrategy = Closure.DELEGATE_FIRST
        configuration.run()
        this[name] = newChannel
    }

    fun channel(map: HashMap<String, Closure<Channel>>) {
        map.forEach { entry: Map.Entry<String, Closure<Channel>> ->
            val name = entry.key
            val configuration = entry.value
            channel(name, configuration)
        }
    }

    /**
     * 浅拷贝
     */
    override fun clone(): ChannelContainer {
        return ChannelContainer(this)
    }
}