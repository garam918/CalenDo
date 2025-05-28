package com.garam.todolist.util.clickListener

import com.garam.todolist.data.Todo

interface PlanItemClickListener {

    fun onPlanTitleClick(plan: Todo)
    fun onPlanEditBtnClick(plan: Todo)

}