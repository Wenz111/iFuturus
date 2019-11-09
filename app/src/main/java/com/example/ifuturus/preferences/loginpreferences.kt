package com.example.ifuturus.preferences

import android.content.Context

class loginpreferences(context: Context) {

    val PREFERENCE_NAME = "LOGIN"
    val PREFERENCES_LOGIN_COUNT = "LOGINCOUNT"

    val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getLoginCount() : Int {
        return preference.getInt(PREFERENCES_LOGIN_COUNT, 5)
    }

    fun setLoginCount(count:Int) {
        val editor = preference.edit()
        editor.putInt(PREFERENCES_LOGIN_COUNT, count)
        editor.apply()
    }
}