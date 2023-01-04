package com.example.common.functions

import android.content.Context
import android.util.Log

const val fileName = "einvoice"
const val tokenKey = "token"


fun saveTokenToSharedPref(androidContext: Context, value: String?) {
    Log.i("sharedPreference", "saveTokenToSharedPref: saved $value to shared preference")
    val sharedPref = androidContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString(tokenKey, value)
        apply()
    }
}

fun loadTokenFromSharedPref(context: Context): String? {
    val sharedPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    return sharedPref.getString(tokenKey, null).also {
        Log.i(
            "sharedPreference",
            "loadTokenFromSharedPref: loaded $it from shared preference"
        )
    }
}