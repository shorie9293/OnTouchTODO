package com.example.ontouchtodo

import java.io.Serializable

class TodoTasks : Serializable {

    var taskName: String = "First Task"

    var taskType: String = "Next action"

    var taskLimit: String = "1900/01/01"

    fun setTaskInformation(task_name: String, task_type: String, task_limit: String) {
        this.taskName = task_name
        this.taskType = task_type
        this.taskLimit = task_limit
    }

}
