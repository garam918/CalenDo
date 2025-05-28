package com.garam.todolist.ui.todoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.data.Category
import com.garam.todolist.databinding.CategoryChangeDialogRecyclerItemLayoutBinding
import com.garam.todolist.util.clickListener.CategoryChangeClickListener
import com.garam.todolist.R

class TodoCategoryChangeRecyclerAdapter(private val categoryId : String, private val clickListener: CategoryChangeClickListener) : ListAdapter<Category,TodoCategoryChangeRecyclerAdapter.ViewHolder>(diffUtil) {

    var selectedCategoryId = categoryId

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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(CategoryChangeDialogRecyclerItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = holder.bind(currentList[position])

    inner class ViewHolder(private val binding : CategoryChangeDialogRecyclerItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {

            binding.category = category

            if(selectedCategoryId == category.categoryId) {
                binding.categoryChangeItemCheckedImg.visibility = View.VISIBLE
                binding.root.background = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_category_change_selected_bg)
            }
            else {
                binding.categoryChangeItemCheckedImg.visibility = View.GONE
                binding.root.background = null
            }

            binding.root.setOnClickListener {

                selectedCategoryId = category.categoryId

                binding.categoryChangeItemCheckedImg.visibility = View.VISIBLE
                binding.root.background = ContextCompat.getDrawable(binding.root.context,R.drawable.todo_category_change_selected_bg)

                clickListener.categoryChangeItemClick(category)


                notifyDataSetChanged()

            }

        }

    }
}