package com.example.ontouchtodo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.ontouchtodo.SortableListView.SimpleDragListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    //    private lateinit var lvMenu: ListView
    private lateinit var lvMenu: SortableListView
    private lateinit var adapter: BaseAdapter
    private val todoTaskList: ArrayList<TodoTasks> = ArrayList()

    private val RESULT_SUBACTIVITY = 1000

    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val helper = DatabaseHelper(this)

        val db = helper.readableDatabase

//        lvMenu = todoList
        val dragListener: SimpleDragListener = SimpleDragListener()

        lvMenu = todoList as SortableListView
        lvMenu.setDragListener(dragListener)
        lvMenu.setSortable(true)

        // input data to todoTasks
        val menuList = TodoTasks()

        todoTaskList.add(menuList)

        try {
            val sql = "select * from todo_table;"

            val cursor = db.rawQuery(sql, null)

            cursor.moveToFirst()

            if (cursor.count > 0) {
                while (!cursor.isAfterLast) {
                    val tn = cursor.getString(1)
                    val tt = cursor.getString(2)
                    val tl = cursor.getString(3)
                    val ml = TodoTasks()
                    ml.setTaskInformation(tn, tt, tl)

                    todoTaskList.add(ml)

                    cursor.moveToNext()
                }
            }

        } catch (exception: Exception) {
            Log.e("selectData", exception.toString())
        }

        this.FAB_todo.setOnClickListener(TodoClickListener())
        //Set adapter
        adapter = MyAdapter(this@MainActivity, todoTaskList)
        lvMenu.adapter = adapter

//        lvMenu.setOnTouchListener(ItemTouchListener())
        lvMenu.onItemClickListener = ItemClickListener()

    }

    // Floating Buttonを押したときのリスナー。todo_input画面開始。
    inner class TodoClickListener : View.OnClickListener {

        override fun onClick(v: View?) {
            val intent: Intent = Intent(this@MainActivity, TodoInputActivity::class.java)

            val todo = TodoTasks()

            intent.putExtra("toIntent", todo)
            startActivityForResult(intent, RESULT_SUBACTIVITY)
        }

    }


    // todo_input画面で入力された情報を再描画する。
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_SUBACTIVITY && null != intent
        ) {

            val todo = intent.getSerializableExtra("toIntent") as TodoTasks

            todoTaskList.add(todo)

            adapter = MyAdapter(this@MainActivity, todoTaskList)
            lvMenu.adapter = adapter

        }
    }

    inner class ItemClickListener : AdapterView.OnItemClickListener, View.OnClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.i("hoge", position.toString())

        }

        override fun onClick(v: View?) {
            Log.i("hoge", v!!.id.toString())
        }
    }


    inner class ItemTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            //           Log.i("hoge", "touch")
            return true
        }

    }

}