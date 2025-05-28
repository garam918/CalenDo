package com.garam.todolist.ui.todoList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.databinding.CategoryIconItemLayoutBinding
import com.garam.todolist.util.clickListener.CategoryIconClickListener


class CategoryIconRecyclerAdapter(private val clickListener: CategoryIconClickListener) : ListAdapter<CategoryIconType, CategoryIconRecyclerAdapter.ViewHolder>(diffUtil) {

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<CategoryIconType>() {

            override fun areItemsTheSame(
                oldItem: CategoryIconType,
                newItem: CategoryIconType
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CategoryIconType,
                newItem: CategoryIconType
            ): Boolean = oldItem == newItem
        }

    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = holder.bind(currentList[position])

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(CategoryIconItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class ViewHolder(private val binding: CategoryIconItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryIconType: CategoryIconType) {
            binding.categoryIconType = categoryIconType

            binding.root.setOnClickListener {

                clickListener.iconClick(categoryIconType)

            }

        }

    }
}