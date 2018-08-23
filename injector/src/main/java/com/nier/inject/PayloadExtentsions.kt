package com.nier.inject

import java.io.IOException
import java.nio.ByteBuffer

/**
 * Created by Nier
 * Date 2018/8/20
 */


internal fun getPayloadById(apk: Apk, id: Int): ByteBuffer? {
    val allPayloads = readPayload(apk)
    return allPayloads[id]
}

/**
 * 读取解析SignBlock中payload中的键值对数据
 */
private fun readPayload(apk: Apk): HashMap<Int, ByteBuffer> {
    if (apk.invalid()) {
        throw IllegalArgumentException("apk not init, do you forget invoke init()")
    }
    apk.channel { apkChannel ->
        //从apk的SignBlock的数据段中读取
        println("signBlockOffset = ${apk.mSignBlockOffset}, signBlockSize = ${apk.mSignBlockSize.toInt()}")
        apkChannel.position(apk.mSignBlockOffset + APK_SIGN_BLOCK_SIZE_BYTE_SIZE)
        val payloadBuffer = allocateBuffer(apk.mSignBlockSize.toInt() - SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE - APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2)
        apkChannel.read(payloadBuffer)
        payloadBuffer.position(0)
        return readValues(payloadBuffer, HashMap())
    }
    throw IOException("unknow exception happened.")
}

/**
 * 递归获取payload中保存的所有键值对
 */
private fun readValues(signBlock: ByteBuffer, values: HashMap<Int, ByteBuffer>): HashMap<Int, ByteBuffer> {
    println("apk sign block remain -> ${signBlock.remaining()}")
    if (signBlock.remaining() < SIGN_BLOCK_PAYLOAD_VALUE_LENGTH_BYTE_SIZE) {
        return values
    }
    val valueSize = signBlock.long
    if (signBlock.remaining() < valueSize || valueSize < SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE) {
        throw IOException("Invalid sign block payload values. sign block size = $valueSize, sign block remain size = ${signBlock.remaining()}")
    }
    val id = signBlock.int

    val content = signBlock.slice(valueSize.toInt() - SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE)
    println("content.position() = ${content.position()}, content.limit() = ${content.limit()}")
    values[id] = content
    return readValues(signBlock, values)
}

/**
 * 添加一个追加信息到SignBlock的payload中
 */
internal fun addPayload(apk: Apk, payload: IExtraPayload) {
    if (apk.invalid()) {
        throw IllegalArgumentException("apk not init, do you forget invoke init()")
    }
    //先将旧的数据读取出来
    val payloads = readPayload(apk)
    //检查是否存在V2的签名
    if (payloads.isEmpty() || payloads[APK_SIGN_V2_KEY] == null) {
        throw IOException("Miss sign v2 block.")
    }
    //构造新的payload数据集并且写入到apk中
    val payloadDataContent = apk.mExtraPayloadHandler.wrap(payload)
    payloads[payload.key()] = payloadDataContent
    writeValues(apk, payloads)

    println("apk is valid = ${verifyApk(apk.sourceDir)}")
}

/**
 * 根据apk的SignBlock协议写入到apk中
 */
private fun writeValues(apk: Apk, payloads: HashMap<Int, ByteBuffer>) {
    apk.channel { channel ->
        println("Before write extra value, apk size = ${channel.size()}")
        //cache sourceTailBlock
        //sourceTailBlock = CentralDirectory + EndOfCentralDirectory
        val sourceRemainAfterSignBlockSize = (channel.size() - apk.mCentralDirectoryStartOffset).toInt()
        val sourceRemainAfterSignBlock = allocateBuffer(sourceRemainAfterSignBlockSize)
        channel.position(apk.mCentralDirectoryStartOffset)
        channel.read(sourceRemainAfterSignBlock)
        sourceRemainAfterSignBlock.flip()

        //跳过开始8子节用于记录SignBlock大小的字段，先进行Payload的写入
        channel.position(apk.mSignBlockOffset + APK_SIGN_BLOCK_SIZE_BYTE_SIZE) //跳过size
        var signBlockLength: Long = 0
        //一次写入新的payload到SignBlock中
        payloads.entries.forEach {
            println("payload value limit = ${it.value.limit()}")
            val payloadLength = it.value.limit() + SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE
            val lengthBuffer = allocateBuffer(SIGN_BLOCK_PAYLOAD_VALUE_LENGTH_BYTE_SIZE).putLong(payloadLength.toLong())
            lengthBuffer.flip()
            val keyBuffer = allocateBuffer(SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE).putInt(it.key)
            keyBuffer.flip()

            channel.write(lengthBuffer)
            channel.write(keyBuffer)
            channel.write(it.value)
            signBlockLength += SIGN_BLOCK_PAYLOAD_VALUE_LENGTH_BYTE_SIZE + payloadLength
        }
        println("after write payload channel position = ${channel.position()}")
        //不包括开头的size，因此这里只加上1个size的长度8
        signBlockLength += APK_SIGN_BLOCK_SIZE_BYTE_SIZE
        signBlockLength += APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2

        //写入末尾的SignBock长度字段
        val signBlockSizeBuffer = allocateBuffer(APK_SIGN_BLOCK_SIZE_BYTE_SIZE)
        signBlockSizeBuffer.putLong(signBlockLength)
        signBlockSizeBuffer.flip()
        channel.write(signBlockSizeBuffer)

        //写入低8位魔数
        var signBlockMagicNumBuffer = allocateBuffer(APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE)
        signBlockMagicNumBuffer.putLong(APK_SIGN_BLOCK_MAGIC_LOW)
        signBlockMagicNumBuffer.flip()
        channel.write(signBlockMagicNumBuffer)

        //写入高8位魔数
        signBlockMagicNumBuffer = allocateBuffer(APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE)
        signBlockMagicNumBuffer.putLong(APK_SIGN_BLOCK_MAGIC_HIGH)
        signBlockMagicNumBuffer.flip()
        channel.write(signBlockMagicNumBuffer)

        //记录新Central directory start offset，稍后需要更新到End of central directory中
        val newCentralDirectoryStartOffset = channel.position()

        //写入头部的SignBock长度字段
        signBlockSizeBuffer.clear()
        signBlockSizeBuffer.putLong(signBlockLength)
        signBlockSizeBuffer.flip()
        channel.position(apk.mSignBlockOffset)
        channel.write(signBlockSizeBuffer)

        //将CentralDirectory直至结尾的数据写入到apk中
        channel.position(newCentralDirectoryStartOffset)
        channel.write(sourceRemainAfterSignBlock)

        //更新End of central directory中记录Central directory start offset的数据段
        val newEOCDSignature = findApkEOCDSignatureOffset(channel)
        val newCDSOBuffer = allocateBuffer(4)
        newCDSOBuffer.putInt(newCentralDirectoryStartOffset.toInt())
        newCDSOBuffer.flip()
        channel.position(calculateCentralDirectoryOffset(newEOCDSignature))
        channel.write(newCDSOBuffer)
    }
}