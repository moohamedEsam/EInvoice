package com.example.domain.auth

import android.content.Context
import com.example.common.functions.loadTokenFromSharedPref

fun interface IsUserLoggedInUseCase : () -> Boolean

fun isUserLoggedInUseCase(context: Context) = IsUserLoggedInUseCase {
    loadTokenFromSharedPref(context) != null
}