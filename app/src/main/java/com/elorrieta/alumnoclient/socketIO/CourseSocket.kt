package com.elorrieta.alumnoclient.socketIO

import android.util.Log
import android.widget.Toast
import com.elorrieta.alumnoclient.CourseListActivity
import com.elorrieta.alumnoclient.entity.Course
import com.elorrieta.alumnoclient.singletons.SocketConnectionManager
import com.elorrieta.alumnoclient.socketIO.config.Events
import com.elorrieta.alumnoclient.socketIO.model.MessageInput
import com.elorrieta.alumnoclient.utils.AESUtil
import com.elorrieta.alumnoclient.utils.JSONUtil
import com.elorrieta.alumnoclient.utils.Util

import io.socket.client.Socket
import org.json.JSONObject

/**
 * The client
 */
class CourseSocket(private val activity: CourseListActivity) {
    private val tag = "socket.io"
    private val socket: Socket = SocketConnectionManager.getSocket()
    private var key = AESUtil.loadKey(activity)

    init {
        socket.on(Events.ON_STUDENT_COURSES_ANSWER.value) { args ->
            Util.safeExecute(tag, activity) {
                val encryptedMessage = args[0] as String
                val decryptedMessage = AESUtil.decrypt(encryptedMessage, key)
                val mi = JSONUtil.fromJson<MessageInput>(decryptedMessage)

                if(mi.code == 200){
                    val coursesJson = JSONObject(mi.message)
                    val coursesArray = coursesJson.getJSONArray("courses")
                    val courses = mutableListOf<Course>()

                    for (i in 0 until coursesArray.length()) {
                        val course = JSONUtil.fromJson<Course>(
                            coursesArray.getJSONObject(i).toString()
                        )
                        Log.d(tag, "Curso: $course")
                        courses.add(course)
                    }
                    onCoursesReceived(courses)
                } else {
                    activity.runOnUiThread {
                        Toast.makeText(
                            activity,
                            "No se han encontrado cursos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun requestCourses() {
        socket.emit(Events.ON_STUDENT_COURSES.value)
        Log.d(tag, "Attempt of get courses")
    }



    private fun onCoursesReceived(courses: List<Course>) {
        activity.runOnUiThread {
            (activity ).updateCourseList(courses)
        }
    }
}
