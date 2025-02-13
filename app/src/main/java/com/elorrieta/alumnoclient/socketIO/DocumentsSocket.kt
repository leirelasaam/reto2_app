package com.elorrieta.alumnoclient.socketIO

import android.util.Log
import com.elorrieta.alumnoclient.DocumentsActivity
import com.elorrieta.alumnoclient.entity.Document
import com.elorrieta.alumnoclient.singletons.LoggedUser
import com.elorrieta.alumnoclient.singletons.PrivateKeyManager
import com.elorrieta.alumnoclient.singletons.SocketConnectionManager
import com.elorrieta.alumnoclient.socketIO.model.MessageInput
import com.elorrieta.alumnoclient.socketIO.model.MessageOutput
import com.elorrieta.alumnoclient.utils.AESUtil
import com.elorrieta.alumnoclient.utils.JSONUtil
import com.elorrieta.alumnoclient.utils.Util
import com.elorrieta.alumnoclient.socketIO.config.Events
import org.json.JSONObject

class DocumentsSocket(private val activity: DocumentsActivity) {
    private var tag = "socket.io"
    private var key = PrivateKeyManager.getKey(activity)
    private val socket = SocketConnectionManager.getSocket()

    init {
        socket.on(Events.ON_STUDENT_DOCUMENTS_ANSWER.value) { args ->
            Util.safeExecute(tag, activity) {
                val encryptedMessage = args[0] as String
                val decryptedMessage = AESUtil.decrypt(encryptedMessage, key)
                val mi = JSONUtil.fromJson<MessageInput>(decryptedMessage)


                if (mi.code == 200) {
                    val documentsJson = JSONObject(mi.message)
                    val documentsArray = documentsJson.getJSONArray("documents")
                    val documents = mutableListOf<Document>()

                    for (i in 0 until documentsArray.length()) {
                        val document = JSONUtil.fromJson<Document>(
                            documentsArray.getJSONObject(i).toString()
                        )
                        documents.add(document)
                    }

                    activity.runOnUiThread {
                        activity.loadAdapter(documents)
                    }
                } else {
                    activity.runOnUiThread {
                        activity.showEmpty()
                    }
                }
            }
        }
    }

    // Custom events
    fun doGetDocumentList() {
        val message = MessageOutput(LoggedUser.user?.id.toString())
        val encryptedMsg = AESUtil.encryptObject(message, key)
        socket.emit(Events.ON_STUDENT_DOCUMENTS.value, encryptedMsg)

        Log.d(tag, "Attempt of get documents - $message")
    }
}