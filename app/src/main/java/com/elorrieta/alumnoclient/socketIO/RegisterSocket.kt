package com.elorrieta.alumnoclient.socketIO

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.elorrieta.alumnoclient.R
import com.elorrieta.alumnoclient.StudentScheduleActivity
import com.elorrieta.alumnoclient.TeacherScheduleActivity
import com.elorrieta.alumnoclient.singletons.LoggedUser
import com.elorrieta.alumnoclient.singletons.LoggedUser.user
import com.elorrieta.alumnoclient.singletons.SocketConnectionManager
import com.elorrieta.alumnoclient.socketIO.config.Events
import com.elorrieta.alumnoclient.socketIO.model.MessageInput
import com.elorrieta.alumnoclient.socketIO.model.MessageRegisterUpdate
import com.elorrieta.alumnoclient.utils.AESUtil
import com.elorrieta.alumnoclient.utils.JSONUtil
import com.elorrieta.alumnoclient.utils.Util
import org.json.JSONObject

class RegisterSocket(private val activity: Activity) {
    private var key = AESUtil.loadKey(activity)
    // For log purposes
    private var tag = "socket.io"
    val socket = SocketConnectionManager.getSocket()

    init {

        // Evento del servidor - Android recibe el usuario logueado
        socket.on(Events.ON_REGISTER_UPDATE_ANSWER.value) { args ->
            Util.safeExecute(tag, activity) {

                var mi: MessageInput? = null

                if (args[0] is JSONObject) {
                    val jsonMessage = args[0] as JSONObject
                    val encryptedMessage = jsonMessage.toString()

                    //Controla si es un mensaje JSON o no, si es un mensaje encryptado lo desencrypta
                    val decryptedMessage = if (encryptedMessage.startsWith("{")) {
                        Log.d("AESUtil", "El mensaje recibido es JSON, no se descifra: $encryptedMessage")
                        encryptedMessage
                    } else {
                        try {
                            AESUtil.decrypt(encryptedMessage, key)
                        } catch (e: Exception) {
                            Log.e("AESUtil", "Error al descifrar el mensaje: $encryptedMessage", e)
                            "Error al descifrar"
                        }
                    }
                    mi = JSONUtil.fromJson<MessageInput>(decryptedMessage)

                    // Aquí sigue el resto del código
                } else {
                    Log.e(tag, "Error: expected JSONObject, but got ${args[0]::class.java}")
                }

                    LoggedUser.user = user
                    Log.d(tag, "User: $user")

                if (mi?.code == 200) {
                    activity.runOnUiThread {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.login_200),
                            Toast.LENGTH_SHORT
                        ).show()

                        // Determina la actividad según el rol del usuario
                        val newActivity = if (user?.role?.role == "profesor") {
                            TeacherScheduleActivity::class.java
                        } else {
                            StudentScheduleActivity::class.java
                        }

                        // Inicia la nueva actividad
                        val intent = Intent(activity, newActivity)
                        activity.startActivity(intent)

                        // Cierra la actividad actual para que no pueda volver atrás
                        activity.finish()
                    }
                }
            else {
                    Log.d("socket", "Error: ${mi?.code}")
                    activity.runOnUiThread {
                        Toast.makeText(
                            activity,
                            "${activity.getString(R.string.register_toast_intent_code)} ${mi?.code} ${mi?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        /* Esto irá en el ON_REGISTER_ANSWER_UPDATE cuando recibamos que se ha actualizado correctamente el usuario en el servidor
    Log.d(tag, "Usuario dado de alta: $jsonMessage")
    Thread.sleep(2000)
  */
    }

    // Custom events
    //Manda un evento con todos los datos comprobados por el usuario
    fun doRegisterUpdate(updateMsg: MessageRegisterUpdate) {
        Log.d(tag, "Attempt of update sign up.${updateMsg.name}")
        Log.d(tag, "Attempt of update sign up.${updateMsg.password}")

        val encryptedMsg = AESUtil.encryptObject(updateMsg, key)

        // Obtener el tamaño del mensaje en KB
        val messageSizeKb = encryptedMsg.toByteArray().size / 1024.0
        Log.d(tag, "Encrypted message size: %.2f KB".format(messageSizeKb))

        socket.emit(Events.ON_REGISTER_UPDATE.value, encryptedMsg)
        Log.d(tag, "Attempt of update sign up.")
    }
}
