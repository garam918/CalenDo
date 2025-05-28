package com.garam.todolist.data

data class CategoryWithTodo(
    val category: Category,
    val todoList: List<Todo>
)