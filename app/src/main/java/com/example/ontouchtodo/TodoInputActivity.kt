package com.example.ontouchtodo

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_todo_input.*

class TodoInputActivity : AppCompatActivity() {

    private lateinit var spinnerText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_input)

        addtodo.setOnClickListener(TodoClickListener())

        val spinner: Spinner = taskType


        tasklimit.setOnClickListener {
            showDataPicker()
        }

        // プルダウン登録
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.task_types)
        )
        spinner.adapter = spinnerAdapter

        // プルダウンのリスナーを登録。
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinnerParent = parent as Spinner
                spinnerText = spinnerParent.selectedItem as String
            }

        }


    }

    // Add todoボタンをクリックすると内容を追加して閉じる。
    inner class TodoClickListener : View.OnClickListener {

        private val tablename = "todo_table"
        private val dbname = "tododatabase.db"

        override fun onClick(p0: View?) {
            val intentSub = Intent()
            val todo = TodoTasks()

            val tn = taskname.text.toString()
            val tl = tasklimit.text.toString()

            todo.setTaskInformation(
                tn,
                spinnerText,
                tl
            )
            intentSub.putExtra("toIntent", todo)
            setResult(Activity.RESULT_OK, intentSub)

            //同時にdbに登録

            val helper: DatabaseHelper = DatabaseHelper(this@TodoInputActivity)
            val db: SQLiteDatabase = helper.writableDatabase

            try {
                //val datetime: LocalDateTime = LocalDateTime.now()
                val values = ContentValues()
                //values.put("id", datetime.toString())
                values.put("task_name", tn)
                values.put("task_type", spinnerText)
                values.put("task_date", tl)

                db.insertOrThrow(tablename, null, values)

            } catch (exception: Exception) {
                Log.e("insertData", exception.toString())
            }


            finish()
        }
    }

    //日付選択ダイアログ
    private fun showDataPicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                tasklimit.text = "${year}/${month + 1}/${dayOfMonth}"
            }, 2020, 5, 1
        )
        datePickerDialog.show()
    }

}
