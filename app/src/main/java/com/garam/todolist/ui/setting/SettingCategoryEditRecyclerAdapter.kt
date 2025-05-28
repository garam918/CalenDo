package com.garam.todolist.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.data.Category
import com.garam.todolist.databinding.SettingCategoryEditItemLayoutBinding
import com.garam.todolist.util.clickListener.SettingCategoryEditClickListener

class SettingCategoryEditRecyclerAdapter(
    val onMoveItem: (from: Int, to: Int) -> Unit,
    private val clickListener: SettingCategoryEditClickListener) : ListAdapter<Category,SettingCategoryEditRecyclerAdapter.ViewHolder>(diffUtil) {

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem.categoryId == newItem.categoryId
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(SettingCategoryEditItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = holder.bind(currentList[position])

    inner class ViewHolder(private val binding: SettingCategoryEditItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {

            binding.category = category

            binding.root.setOnClickListener {

                clickListener.categoryEditBtnClick(category)


            }

        }

    }
}