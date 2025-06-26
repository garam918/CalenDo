package com.garam.todolist.ui.todoList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.data.CategoryWithTodo
import com.garam.todolist.databinding.TodoCategoryItemCardLayoutBinding
import com.garam.todolist.util.CategoryClickListener
import com.garam.todolist.util.clickListener.TodoClickListener
import com.garam.todolist.util.functions.filterTodosByDate
import com.garam.todolist.util.functions.showKeyboard
import java.time.LocalDate
import com.garam.todolist.R
import com.garam.todolist.data.Todo
import com.garam.todolist.data.TodoStatus

class TodoListCategoryRecyclerAdapter(private val categoryClickListener : CategoryClickListener,
                                      private val todoClickListener: TodoClickListener,
                                      private val selectedDate : LocalDate,
                                      private val sortMode : String) : ListAdapter<CategoryWithTodo, TodoListCategoryRecyclerAdapter.ViewHolder>(diffUtil) {

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<CategoryWithTodo>() {

            override fun areItemsTheSame(
                oldItem: CategoryWithTodo,
                newItem: CategoryWithTodo
            ): Boolean = oldItem.category.categoryId == newItem.category.categoryId

            override fun areContentsTheSame(
                oldItem: CategoryWithTodo,
                newItem: CategoryWithTodo
            ): Boolean = oldItem == newItem

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(currentList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(TodoCategoryItemCardLayoutBinding.inflate(
        LayoutInflater.from(parent.context), parent, false))

    inner class ViewHolder(private val binding : TodoCategoryItemCardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryWithTodo: CategoryWithTodo) {

            binding.categoryWithTodo = categoryWithTodo

            binding.todoAddBtn.setOnClickListener {

                categoryClickListener.todoAdd(categoryWithTodo.category.categoryId)

            }

            val adapter = TodoListCategoryInnerRecyclerAdapter(object : TodoClickListener {
                override fun todoCheckBoxClick(
                    todo: Todo,
                    position: Int
                ) {
                    todoClickListener.todoCheckBoxClick(todo,absoluteAdapterPosition)

                }

                override fun todoEditBtnClick(todo: Todo) {
                    todoClickListener.todoEditBtnClick(todo)
                }

                override fun todoTitleEditClick(todo: Todo) {
                    todoClickListener.todoTitleEditClick(todo)
                }

                override fun submitTodoCount(categoryId: String, count: Int) {
                    todoClickListener.submitTodoCount(categoryWithTodo.category.categoryId, count)
                }
            }
//                todoClickListener
                ,selectedDate.toString())
            binding.categoryTodoListRecyclerView.adapter = adapter

            val list = filterTodosByDate(categoryWithTodo.todoList.toMutableList(), LocalDate.parse(selectedDate.toString()))

            adapter.submitList(list.sortedByDescending {
                val priorityRank = if (it.priority == true) 1L else 0L

                val sortRank = when(sortMode) {
                    "Saved" -> {
                        it.savedTime.toInstant().epochSecond
                    }
                    "Completed_Reversed" -> {
                        val isCompleted = it.status?.values?.contains(TodoStatus.COMPLETED) == true
                        val completedRank = if (isCompleted) 0 else 1
                        completedRank * 1_000_000_000L + it.savedTime.toInstant().epochSecond
                    }
                    "Completed" -> {
                        val isCompleted = it.status?.values?.contains(TodoStatus.COMPLETED) == true
                        val completedRank = if (isCompleted) 1 else 0
                        completedRank * 1_000_000_000L + it.savedTime.toInstant().epochSecond
                    }
                    else -> {
                        it.savedTime.toInstant().epochSecond
                    }
                }

                priorityRank * 1_000_000_000_000_000_000 + sortRank
            }) {

                todoClickListener.submitTodoCount(categoryWithTodo.category.categoryId, list.size)

//                binding.categoryTodoListRecyclerView.post {
//                    val lastIndex = list.lastIndex
//                    val vh = binding.categoryTodoListRecyclerView.findViewHolderForAdapterPosition(lastIndex)
//                    val editText = vh?.itemView?.findViewById<EditText>(R.id.todo_item_title_edit_text)  // 해당 ID로 교체
//                    editText?.requestFocus()
//
//                    val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
//                }

            }
        }
    }
}