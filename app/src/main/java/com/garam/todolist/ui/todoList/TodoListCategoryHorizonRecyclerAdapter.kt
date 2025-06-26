package com.garam.todolist.ui.todoList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.databinding.TodoCategoryItemLayoutBinding
import com.garam.todolist.data.Category
import com.garam.todolist.util.clickListener.CategoryFilterClickListener

class TodoListCategoryHorizonRecyclerAdapter(private val filterClickListener: CategoryFilterClickListener) : ListAdapter<Category, TodoListCategoryHorizonRecyclerAdapter.ViewHolder>(diffUtil) {

    var currentPosition = -1

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<Category>() {

            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem.categoryId == newItem.categoryId

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem == newItem

        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(currentList[position])

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(TodoCategoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    inner class ViewHolder(private val binding : TodoCategoryItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {

            binding.category = category

            if(currentPosition == absoluteAdapterPosition) binding.root.alpha = 1f
            else binding.root.alpha = 0.4f

            binding.root.setOnClickListener {

                currentPosition = absoluteAdapterPosition

                notifyDataSetChanged()

                binding.root.alpha = 1f
                filterClickListener.categoryFilter(category,absoluteAdapterPosition)

            }
        }

    }


}