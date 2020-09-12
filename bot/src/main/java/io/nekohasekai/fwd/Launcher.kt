package io.nekohasekai.fwd

import io.nekohasekai.ktlib.core.defaultLog
import io.nekohasekai.ktlib.td.cli.TdCli
import io.nekohasekai.ktlib.td.core.extensions.displayNameFormatted
import io.nekohasekai.ktlib.td.core.extensions.text
import io.nekohasekai.ktlib.td.core.raw.*
import td.TdApi
import java.io.File

object Launcher : TdCli() {

    override val loginType = LoginType.USER

    override var configFile = File("fwd.yml")

    @JvmStatic
    fun main(args: Array<String>) {

        launch(args)

        loadConfig()

        start()

    }

    var last = ""

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        if (userId == 1162660847) {

            defaultLog.info("[${getChat(chatId).title} $chatId] ${getUserOrNull(userId)?.displayNameFormatted ?: "$userId"}: ${message.text ?: ("[" + message.content.javaClass.simpleName.substringAfter("Message") + "]")}")

            val text = message.text

            if (text == null || message.forwardInfo != null) {

                forwardMessages(me.id.toLong(), chatId, longArrayOf(message.id), TdApi.MessageSendOptions(), asAlbum = false, sendCopy = false, removeCaption = false)

                return

            }

            if (text.replace(" ", "")
                            .replace("\n", "") == last) return

            if (text.split("\n").any { last.split("\n").contains(it) }) return

            last = text

            if (text.replace(" ", "").replace("\n", "").contains("(香港|台湾)".toRegex())) return

            if (text.count { it == '\n' } > 2) return

            forwardMessages(-1001455198496, chatId, longArrayOf(message.id), TdApi.MessageSendOptions(), asAlbum = false, sendCopy = false, removeCaption = false)

        } else {

            defaultLog.trace("[${getChat(chatId).title} $chatId] ${getUserOrNull(userId)?.displayNameFormatted ?: "$userId"}: ${message.text ?: ("[" + message.content.javaClass.simpleName.substringAfter("Message") + "]")}")

        }

    }

}