package com.garam.todolist.ui.todoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.databinding.PlanItemLayoutBinding

import com.garam.todolist.data.Todo
import com.garam.todolist.util.clickListener.PlanItemClickListener

class PlanListRecyclerAdapter(private val planItemClickListener: PlanItemClickListener)
    : ListAdapter<Todo,PlanListRecyclerAdapter.ViewHolder>(diffUtil) {

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
    ): ViewHolder = ViewHolder(PlanItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = holder.bind(currentList[position])

    inner class ViewHolder(private val binding: PlanItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: Todo) {

            binding.plan = plan

            // 제목 편집 중일 때 다른 아이템 투명도 설정

            binding.planTitleEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if(!hasFocus) {
                    planItemClickListener.onPlanTitleClick(plan.copy(title = binding.planTitleEditText.text.toString()))
                }
            }

            binding.planEditBtn.setOnClickListener {

                planItemClickListener.onPlanEditBtnClick(plan)
            }


        }
    }
}