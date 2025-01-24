package com.elorrieta.alumnoclient.utils

import android.annotation.SuppressLint
import android.content.Context
import com.elorrieta.alumnoclient.R
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AESUtil {

    // Comando para regenerar la clave desde consola: openssl rand -out aes.key 32
    // Para cargar la clave, solo se va a ejecutar una vez al iniciar el servidor
    @Throws(FileNotFoundException::class, IOException::class)
    fun loadKey(context: Context): SecretKey {
        var key: SecretKey? = null
        try {
            val inputStream: InputStream = context.resources.openRawResource(R.raw.aes)
            val bytes = inputStream.readBytes()

            if (bytes.isEmpty()) {
                throw IOException("El archivo de clave está vacío.")
            }

            key = SecretKeySpec(bytes, "AES")
        } catch (e: FileNotFoundException) {
            throw e
        } catch (e: IOException) {
            throw e
        }
        return key
    }

    // Cifrado AES
    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    fun encrypt(data: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val encryptedData = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedData)
    }

    // Descifrado AES
    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    fun decrypt(encryptedData: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)

        val decodedData = Base64.getDecoder().decode(encryptedData)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData)
    }
}
