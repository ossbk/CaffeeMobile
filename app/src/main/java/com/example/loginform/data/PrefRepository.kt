package com.example.loginform.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private var prefs = context.getSharedPreferences("mprefs", Context.MODE_PRIVATE)

    var currentUser: String
        get() = prefs.getString("currentUser", "").toString()
        set(value) {
            prefs.edit().putString("currentUser", value).apply()
        }

}