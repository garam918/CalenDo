package com.garam.todolist.data.source

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedPreferenceStorage(context: Context) {

    private val prefs = context.getSharedPreferences("screen_settings", Context.MODE_PRIVATE)

    private val startModeFlow = MutableStateFlow(prefs.getString("start_screen_mode", "CalenDo"))
    private val sortModeFlow = MutableStateFlow(prefs.getString("sort_mode","Saved"))
    private val firstDayOfWeekFlow = MutableStateFlow(prefs.getString("first_day_of_week","Mon"))

    fun saveStartScreenMode(startMode: String) {
        prefs.edit().putString("start_screen_mode", startMode).apply()
        startModeFlow.value = startMode
    }

    fun getStartScreenModeFlow(): StateFlow<String?> = startModeFlow.asStateFlow()

    fun saveSortMode(sortMode: String) {
        prefs.edit().putString("sort_mode", sortMode).apply()
        Log.e("spfSortMode",sortMode)
        sortModeFlow.value = sortMode
    }

    fun getSortModeFlow(): StateFlow<String?> = sortModeFlow.asStateFlow()

    fun saveFirstDayOfWeek(firstDayOfWeek: String) {
        prefs.edit().putString("first_day_of_week", firstDayOfWeek).apply()
        firstDayOfWeekFlow.value = firstDayOfWeek
    }

    fun getFirstDayOfWeekFlow(): StateFlow<String?> = firstDayOfWeekFlow.asStateFlow()
}