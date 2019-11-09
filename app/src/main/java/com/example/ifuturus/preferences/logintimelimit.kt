package com.example.ifuturus.preferences

import android.content.Context

class logintimelimit(context: Context) {

    val PREFERENCE_TIME = "TIME"
    val PREFERENCE_TIME_LIMIT = "TIMELIMIT"

    val preference = context.getSharedPreferences(PREFERENCE_TIME, Context.MODE_PRIVATE)

    fun getTimeLimit() : String {
        return preference.getString(PREFERENCE_TIME_LIMIT, "")!!
    }

    fun setTimeLimit(timeLimit:String) {
        val editor = preference.edit()
        editor.putString(PREFERENCE_TIME_LIMIT, timeLimit)
        editor.apply()
    }
}