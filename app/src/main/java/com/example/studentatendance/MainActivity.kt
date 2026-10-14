package com.example.studentatendance

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


data class Student(var name: String, var status: String = "None") // None, Present, Absent

class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnReset: Button
    private lateinit var listView: ListView
    private lateinit var tvTotal: TextView
    private lateinit var tvPresent: TextView
    private lateinit var tvAbsent: TextView
    private lateinit var tvEmpty: TextView

    private val studentList = ArrayList<Student>()
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etName = findViewById(R.id.etStudentName)
        btnAdd = findViewById(R.id.btnAdd)
        btnReset = findViewById(R.id.btnReset)
        listView = findViewById(R.id.listViewStudents)
        tvTotal = findViewById(R.id.tvTotal)
        tvPresent = findViewById(R.id.tvPresent)
        tvAbsent = findViewById(R.id.tvAbsent)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = StudentAdapter()
        listView.adapter = adapter

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter student name!", Toast.LENGTH_SHORT).show()
            } else {
                studentList.add(Student(name))
                etName.text.clear()
                adapter.notifyDataSetChanged()
                updateSummary()
                Toast.makeText(this, "$name added", Toast.LENGTH_SHORT).show()
            }
        }

        btnReset.setOnClickListener {
            if (studentList.isEmpty()) {
                Toast.makeText(this, "Register is already empty", Toast.LENGTH_SHORT).show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Reset Register")
                    .setMessage("Are you sure you want to clear all students?")
                    .setPositiveButton("Yes") { _, _ ->
                        studentList.clear()
                        adapter.notifyDataSetChanged()
                        updateSummary()
                        Toast.makeText(this, "Register cleared", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }

        updateSummary()
    }

    private fun updateSummary() {
        val total = studentList.size
        val present = studentList.count { it.status == "Present" }
        val absent = studentList.count { it.status == "Absent" }

        tvTotal.text = "Total: $total"
        tvPresent.text = "Present: $present"
        tvAbsent.text = "Absent: $absent"

        if (total == 0) {
            tvEmpty.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }


    inner class StudentAdapter : BaseAdapter() {
        override fun getCount(): Int = studentList.size
        override fun getItem(position: Int): Any = studentList[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView?: LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.item_student, parent, false)

            val tvName = view.findViewById<TextView>(R.id.tvStudentName)
            val rgStatus = view.findViewById<RadioGroup>(R.id.rgStatus)
            val rbPresent = view.findViewById<RadioButton>(R.id.rbPresent)
            val rbAbsent = view.findViewById<RadioButton>(R.id.rbAbsent)

            val student = studentList[position]
            tvName.text = "${position + 1}. ${student.name}"

            rgStatus.setOnCheckedChangeListener(null)
            rgStatus.clearCheck()
            if (student.status == "Present") rgStatus.check(R.id.rbPresent)
            if (student.status == "Absent") rgStatus.check(R.id.rbAbsent)

            // Color coding Task 3
            when (student.status) {
                "Present" -> view.setBackgroundColor(0xFFE8F5E9.toInt()) // light green
                "Absent" -> view.setBackgroundColor(0xFFFFEBEE.toInt()) // light red
                else -> view.setBackgroundColor(0xFFFFFFFF.toInt()) // white
            }

            rgStatus.setOnCheckedChangeListener { _, checkedId ->
                student.status = when (checkedId) {
                    R.id.rbPresent -> "Present"
                    R.id.rbAbsent -> "Absent"
                    else -> "None"
                }
                updateSummary()
                notifyDataSetChanged()
            }

            return view
        }
    }
}