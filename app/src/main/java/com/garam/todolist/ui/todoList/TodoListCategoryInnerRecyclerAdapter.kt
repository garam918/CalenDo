package com.garam.todolist.ui.todoList

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.garam.todolist.R
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
import com.garam.todolist.databinding.TodoItemLayoutBinding
import com.garam.todolist.util.clickListener.TodoClickListener


class TodoListCategoryInnerRecyclerAdapter(private val todoClickListener : TodoClickListener,
    private val selectedDate : String) : ListAdapter<Todo,TodoListCategoryInnerRecyclerAdapter.ViewHolder>(diffUtil) {

    var onItemInserted: (() -> Unit)? = null

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

    override fun submitList(list: List<Todo>?) {
        super.submitList(list) {
            onItemInserted?.invoke()
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = holder.bind(currentList[position])

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(TodoItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class ViewHolder(private val binding : TodoItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo) {

            binding.todo = todo

            binding.todoItemMoreBtn.setOnClickListener {

                todoClickListener.todoEditBtnClick(todo.copy(title = binding.todoItemTitleEditText.text.toString()))

            }

            when(todo.status?.get(selectedDate)) {
                TodoStatus.NONE -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_none_icon)
                    binding.todoItemTitleEditText.alpha = 0.45f

                }
                TodoStatus.IN_PROGRESS -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_in_progress_icon)
                    binding.todoItemCheckBox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(binding.root.context,R.color.main_color), PorterDuff.Mode.SRC_ATOP
                    )
                    binding.todoItemTitleEditText.alpha = 1f
                }
                TodoStatus.COMPLETED -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_completed_icon)
                    binding.todoItemCheckBox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(binding.root.context,R.color.main_color), PorterDuff.Mode.SRC_ATOP
                    )
                    binding.todoItemTitleEditText.alpha = 1f
                }
                else -> {
                    binding.todoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_none_icon)
                    binding.todoItemTitleEditText.alpha = 0.45f
                    
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