package com.elorrieta.alumnoclient

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.elorrieta.alumnoclient.entity.LoggedUser
import com.elorrieta.alumnoclient.socketIO.HomeTeacherSocket
import com.elorrieta.alumnoclient.socketIO.LoginSocket

class HomeTeacherActivity : AppCompatActivity() {
    private var socketClient: HomeTeacherSocket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("HOME", LoggedUser.user.toString());

        socketClient = HomeTeacherSocket(this)
        socketClient!!.connect()

        findViewById<Button>(R.id.btnGetSchedules)
            .setOnClickListener {
                socketClient!!.doGetSchedules()
            }
    }
}