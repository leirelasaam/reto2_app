package com.elorrieta.alumnoclient


import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elorrieta.alumnoclient.entity.Course
import com.elorrieta.alumnoclient.socketIO.CourseSocket
import com.elorrieta.alumnoclient.socketIO.HomeTeacherSocket
import java.text.SimpleDateFormat
import java.util.Date


class CourseListActivity : BaseActivity() {

    private var socketClient: CourseSocket? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private var courses: List<Course> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = layoutInflater
        val contentView = inflater.inflate(R.layout.activity_course_list, null)
        findViewById<FrameLayout>(R.id.content_frame).addView(contentView)

        socketClient = CourseSocket(this)
        socketClient!!.requestCourses()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        courseAdapter = CourseAdapter(this, courses)

        recyclerView.adapter = courseAdapter



    }
    fun parseDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.parse(dateString) ?: Date()
    }
    fun updateCourseList(courses: List<Course>) {
        this.courses = courses
        courseAdapter.updateCourses(courses)
    }
}
