package com.garam.todolist.ui.todoList

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.data.Todo
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.util.clickListener.TodoClickListener
import com.garam.todolist.databinding.TodoItemInGoalLayoutBinding
import com.garam.todolist.R


class TodoListInGoalRecyclerAdapter(private val todoClickListener : TodoClickListener,
    private val selectedDate : String) : ListAdapter<Todo,TodoListInGoalRecyclerAdapter.ViewHolder>(diffUtil) {

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<Todo>() {

            override fun areItemsTheSame(
                oldItem: Todo,
                newItem: Todo
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Todo,
                newItem: Todo
            ): Boolean = oldItem == newItem
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoListInGoalRecyclerAdapter.ViewHolder = ViewHolder(TodoItemInGoalLayoutBinding.inflate(
        LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: TodoListInGoalRecyclerAdapter.ViewHolder,
        position: Int
    ) = holder.bind(currentList[position])

    inner class ViewHolder(private val binding: TodoItemInGoalLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo) {

            binding.todo = todo

            binding.todoItemMoreBtn.setOnClickListener {

                todoClickListener.todoEditBtnClick(todo.copy(title = binding.todoItemTitleEditText.text.toString()))

            }

            when(todo.status?.get(selectedDate)) {
                TodoStatus.NONE -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.goal_todo_status_none_icon)
                }
                TodoStatus.IN_PROGRESS -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.goal_todo_status_in_progress_icon)
                    binding.todoItemCheckBox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(binding.root.context,R.color.color_gray0), PorterDuff.Mode.SRC_ATOP
                    )
                }
                TodoStatus.COMPLETED -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.goal_todo_status_completed_icon)
                    binding.todoItemCheckBox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(binding.root.context,R.color.color_gray0), PorterDuff.Mode.SRC_ATOP
                    )
                }
                else -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.goal_todo_status_none_icon)

                }

            }

            binding.todoItemCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(
                    p0: CompoundButton?,
                    p1: Boolean
                ) {
                    todoClickListener.todoCheckBoxClick(todo.copy(title = binding.todoItemTitleEditText.text.toString()), absoluteAdapterPosition)
                    notifyItemChanged(absoluteAdapterPosition)
                }
            })

            binding.todoItemTitleEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->

                if(!hasFocus) {
                    todoClickListener.todoTitleEditClick(todo.copy(title = binding.todoItemTitleEditText.text.toString()))
                }

            }
        }
    }
}