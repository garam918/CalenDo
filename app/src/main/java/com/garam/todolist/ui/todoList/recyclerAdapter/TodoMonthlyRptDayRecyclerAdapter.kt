package com.garam.todolist.ui.todoList.recyclerAdapter

import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.remove
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.data.TodoMonthlyRptDayClass
import com.garam.todolist.databinding.TodoMonthlyRepeatDayItemLayoutBinding
import com.garam.todolist.databinding.TodoMonthlyRepeatLastDayItemLayoutBinding
import com.garam.todolist.util.clickListener.MonthlyRptDayClickListener


class TodoMonthlyRptDayRecyclerAdapter(private val clickListener: MonthlyRptDayClickListener) : ListAdapter<TodoMonthlyRptDayClass, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        const val TYPE_DATE = 0
        const val TYPE_LAST = 1

        val diffUtil = object : DiffUtil.ItemCallback<TodoMonthlyRptDayClass>() {

            override fun areItemsTheSame(
                oldItem: TodoMonthlyRptDayClass,
                newItem: TodoMonthlyRptDayClass
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: TodoMonthlyRptDayClass,
                newItem: TodoMonthlyRptDayClass
            ): Boolean = oldItem.day == oldItem.day
        }

    }

    override fun getItemViewType(position: Int): Int = if(getItem(position).day == "-1") TYPE_LAST
    else TYPE_DATE


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = if(viewType == TYPE_DATE) ViewHolder(TodoMonthlyRepeatDayItemLayoutBinding.inflate(
        LayoutInflater.from(parent.context),parent,false))
    else LastDayViewHolder(TodoMonthlyRepeatLastDayItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) = if(getItem(position).day != "-1") (holder as ViewHolder).bind(currentList[position])
    else (holder as LastDayViewHolder).bind(currentList[position])


    inner class ViewHolder(val binding: TodoMonthlyRepeatDayItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: TodoMonthlyRptDayClass) {
            binding.day = day


            binding.root.setOnClickListener {

                clickListener.dayClick(day)

                notifyItemChanged(absoluteAdapterPosition)
            }

        }
    }

    inner class LastDayViewHolder(val binding : TodoMonthlyRepeatLastDayItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day : TodoMonthlyRptDayClass) {

            binding.day = day

            binding.root.setOnClickListener {
                clickListener.dayClick(day)

                notifyItemChanged(absoluteAdapterPosition)
            }

        }

    }
}