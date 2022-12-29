package com.example.auth.domain

import com.example.common.Result

fun interface LogoutUseCase : suspend () -> Result<Unit>