package top.ncserver.chatsync.Until

import net.mamoe.mirai.console.data.*


object Config : AutoSavePluginConfig("Config") {

    var port: Int by value(1111)
    var groupID: Long by value(0L)
    var msgStyle: String by value("みさか(%s):\"%msg\"")
    var syncMsg:Boolean by value(true)
    var banCommand:List<String> by value()


}
