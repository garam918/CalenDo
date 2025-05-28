package com.garam.todolist.util.clickListener

import com.garam.todolist.data.Todo

interface TodoClickListener {

    fun todoEditBtnClick(todo: Todo)
    fun todoCheckBoxClick(todo: Todo, position: Int)
    fun todoTitleEditClick(todo: Todo)

    fun submitTodoCount(categoryId: String, count: Int)
}