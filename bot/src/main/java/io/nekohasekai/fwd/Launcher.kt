package io.nekohasekai.fwd

import io.nekohasekai.ktlib.core.defaultLog
import io.nekohasekai.ktlib.td.cli.TdCli
import io.nekohasekai.ktlib.td.core.TdException
import io.nekohasekai.ktlib.td.core.extensions.displayNameFormatted
import io.nekohasekai.ktlib.td.core.extensions.text
import io.nekohasekai.ktlib.td.core.raw.*
import io.nekohasekai.ktlib.td.core.utils.getChats
import kotlinx.coroutines.*
import td.TdApi
import java.io.File
import kotlin.system.exitProcess

object Launcher : TdCli() {

    override val loginType = LoginType.USER

    override var configFile = File("fwd.yml")

    @JvmStatic
    fun main(args: Array<String>) {

        launch(args)

        loadConfig()

        start()

    }

    var fromUser = 0
    var toChat = 0L

    override fun onLoadConfig() {

        super.onLoadConfig()

        fromUser = intConfig("FROM_USER") ?: fromUser
        toChat = longConfig("TO_CHAT") ?: toChat

        if (fromUser == 0 || toChat == 0L) {

            defaultLog.error("configured.")

            exitProcess(1)

        }

    }

    override suspend fun onLogin() {

        GlobalScope.launch(Dispatchers.IO) {

            waitForLogin()

            try {

                defaultLog.info("Forward To: " + getChat(toChat).title + " ($toChat)")

            } catch (e: TdException) {

                getChats()

                try {

                    defaultLog.info("Forward To: " + getChat(toChat).title + " ($toChat)")

                } catch (e: TdException) {

                    defaultLog.error(e, "Unable to get target chat: ")

                }

            }

        }

    }

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        if (userId == fromUser) {

            forwardMessages(toChat, chatId, longArrayOf(message.id), TdApi.MessageSendOptions(), asAlbum = false, sendCopy = false, removeCaption = false)

            defaultLog.info("[${getChat(chatId).title} $chatId] ${getUserOrNull(userId)?.displayNameFormatted ?: "$userId"}: ${message.text ?: ("[" + message.content.javaClass.simpleName.substringAfter("Message") + "]")}")

        } else {

            defaultLog.trace("[${getChat(chatId).title} $chatId] ${getUserOrNull(userId)?.displayNameFormatted ?: "$userId"}: ${message.text ?: ("[" + message.content.javaClass.simpleName.substringAfter("Message") + "]")}")

        }

    }

}