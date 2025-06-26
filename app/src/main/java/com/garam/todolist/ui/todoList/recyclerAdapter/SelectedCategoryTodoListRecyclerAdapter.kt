package com.garam.todolist.ui.todoList.recyclerAdapter

import android.graphics.PorterDuff
import com.garam.todolist.R
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.data.Todo
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.databinding.SelectedCategoryTodoListItemLayoutBinding
import com.garam.todolist.util.clickListener.TodoClickListener

class SelectedCategoryTodoListRecyclerAdapter(private val todoClickListener : TodoClickListener,
                                              private val selectedDate : String) : ListAdapter<Todo, SelectedCategoryTodoListRecyclerAdapter.ViewHolder>(diffUtil) {

    companion object {

        val diffUtil = object :  DiffUtil.ItemCallback<Todo>() {

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
    ): SelectedCategoryTodoListRecyclerAdapter.ViewHolder = ViewHolder(
        SelectedCategoryTodoListItemLayoutBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: SelectedCategoryTodoListRecyclerAdapter.ViewHolder,
        position: Int
    ) = holder.bind(currentList[position])

    inner class ViewHolder(private val binding: SelectedCategoryTodoListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo) {

            binding.todo = todo

            when(todo.status?.get(selectedDate)) {
                TodoStatus.NONE -> {
                    binding.selectedCategoryTodoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_none_icon)
                    binding.selectedCategoryTodoItemTitleEditText.alpha = 0.45f

                }
                TodoStatus.IN_PROGRESS -> {
                    binding.selectedCategoryTodoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_in_progress_icon)
                    binding.selectedCategoryTodoItemCheckBox.buttonDrawable?.colorFilter =
                        PorterDuffColorFilter(
                            ContextCompat.getColor(binding.root.context,R.color.main_color), PorterDuff.Mode.SRC_ATOP
                        )
                    binding.selectedCategoryTodoItemTitleEditText.alpha = 1f
                }
                TodoStatus.COMPLETED -> {
                    binding.selectedCategoryTodoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_completed_icon)
                    binding.selectedCategoryTodoItemCheckBox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(binding.root.context,R.color.main_color), PorterDuff.Mode.SRC_ATOP
                    )
                    binding.selectedCategoryTodoItemTitleEditText.alpha = 1f
                }
                else -> {
                    binding.selectedCategoryTodoItemCheckBox.buttonDrawable = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_status_none_icon)
                    binding.selectedCategoryTodoItemTitleEditText.alpha = 0.45f

                }

            }

            binding.selectedCategoryTodoItemCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(
                    p0: CompoundButton?,
                    p1: Boolean
                ) {

                    todoClickListener.todoCheckBoxClick(todo.copy(title = binding.selectedCategoryTodoItemTitleEditText.text.toString()), absoluteAdapterPosition)
//                    notifyItemChanged(absoluteAdapterPosition)

                }
            })

            binding.selectedCategoryTodoItemTitleEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if(!hasFocus) {

                    todoClickListener.todoTitleEditClick(todo.copy(title = binding.selectedCategoryTodoItemTitleEditText.text.toString()))
                }

            }



            binding.selectedCategoryTodoItemEditBtn.setOnClickListener {

                todoClickListener.todoEditBtnClick(todo.copy(title = binding.selectedCategoryTodoItemTitleEditText.text.toString()))

            }
        }

    }
}