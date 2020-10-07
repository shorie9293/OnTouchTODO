package com.example.ontouchtodo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.ontouchtodo.R.layout.row
import kotlinx.android.synthetic.main.row.view.*

// List Adapter for TodoTasks.
class MyAdapter(context: Context, private val tl: ArrayList<TodoTasks>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(p0: Int): Any {
        return tl[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return tl.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1
        var holder: ViewHolder
        /*
         input task information to ViewHolder.
         View has the tag information of tasks.
        */
        if (view == null) {
            view = inflater.inflate(row, null)
            holder = ViewHolder(
                view.taskName,
                view.tasktype,
                view.taskLimit
            )
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.taskName.text = tl[p0].taskName
        holder.taskType.text = tl[p0].taskType
        holder.taskLimit.text = tl[p0].taskLimit

        view!!.btdel.setOnClickListener(DelButtonClickListener(view))
        view.taskName.setOnClickListener(CheckClickListener(view))

        return view

    }

    //Class for keeping data of tasks as tag
    data class ViewHolder(val taskName: TextView, val taskType: TextView, val taskLimit: TextView)


    //Checkboxをクリックしたときに色を変える
    inner class CheckClickListener(view: View) : View.OnClickListener {

        private val view = view

        @SuppressLint("ResourceAsColor")
        override fun onClick(v: View?) {
            val check = v!!.taskName.isChecked

            if (check) {
                view.taskinformation.setBackgroundColor(Color.GRAY)
            } else {
                view.taskinformation.setBackgroundColor(Color.WHITE)
            }
        }


    }

    inner class DelButtonClickListener(private val view: View) : View.OnClickListener {

        override fun onClick(v: View?) {

        }


    }

}
