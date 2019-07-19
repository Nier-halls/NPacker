# NPacker
##提纲
* NPacker实现的原理 apk sign v2
    * sign v2版本Apk的内部结构
    * 如何在修改Apk的情况下不破坏签名的合法性
* NPacker具体实现
    * Task的依赖创建，extensions的创建
    * InjectTask的实现
    * 渠道信息的注入
        * 根据apk文件找到End Of Central Directory (0x06054b50)
        * 根据EOCD找到Central Data Start
        * 根据Central Data Start找到Sign Block
        * 读取Sign Block中原有的Payload
        * 加入合并新的自定义的Payload
        * 重新写回到Sign Block中
        * 恢复Central Data Start往后的所有数据，刷新EOCD中的Central Data Start Offset字段
* NPacker后续需要改进点 future
    * PayloadSupport和ApkSupport的职责划分
    * 日志权限的统一
    * python脚本的实现版本
    * python脚本的读取渠道信息版本
    
##NPacker实现的原理
###APK Sign v2
Android APK在Sign v1版本的时候是标准的ZIP文件，签名信息都放在了META-INF中，而在Sign v2版本的时候APK文件中多了一块Sign Block的数据区块

![APK Sign Struct](./apk_sign_struct.png)

Sign v2签名把整个APk文件分成若干个chunk，然后对chunk进行签名，最后再将这些chunk签名汇总在进行一次签名，最后将签名信息保存到Sign Block块中。APK中第一三四块数据都会受到签名的保护，确保不被篡改。

![APK Sign Protection](./apk_integrity_protection.png)

> Protection of section 4 (ZIP End of Central Directory) is complicated by the section containing the offset of ZIP Central Directory. The offset changes when the size of the APK Signing Block changes, for instance, when a new signature is added. Thus, when computing digest over the ZIP End of Central Directory, the field containing the offset of ZIP Central Directory must be treated as containing the offset of the APK Signing Block.

官方文档中提到第四部分End of Central Directory比较特殊，它里面保存着Central Directory的偏移量，Central Directory紧挨着SignBlock，因此SignBlock发生改变时End of Central Directory中记录Central Directory的偏移量的字段也将会发生改变，因此在计算最后一块数据（EOCD）时会排除掉这个字段来计算。也就是说Sign v2允许你来修改Sign Block以及EOCD中的Central Directory offset字段

NPacker就是利用这点向SignBlock v2数据段中写入数据而不影响APK签名有效性来完成渠道信息的注入，从而达到打一个包复制出多个渠道包分别写入渠道信息来提高打包效率

###NPacker具体实现
####Task创建
NPacker模拟Android的Assemble创建了4类Task，分别是[Root]_Task、[BuildType]_Task、[Flavor]_Task、[Channel]_Task;

它们之间的依赖关系如下
```
    [Root]_Task       dependsOn  [BuildType]_Task
    [BuildType]_Task  dependsOn  [Flavor]_Task
    [Flavor]_Task     dependsOn  [Channel]_Task
    [Channel]_Task    dependsOn  variant.assemble
```
Root、BuildType、Flavor：用来区别多渠道打包的范围，Root打所有渠道包，BuildType指定BuildType的所有Flavor的所有渠道包，Flavor打指定Flavor的所有BuildType的所有渠道包
ChannelTask：用来完成一次APK拷贝并且向SignBlock中注入渠道信息的工作
variant.assemble: Android一个渠道的编译打包Task

####ChannelTask的实现