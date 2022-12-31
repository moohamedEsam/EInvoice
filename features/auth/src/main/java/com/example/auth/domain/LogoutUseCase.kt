package com.example.auth.domain

import com.example.common.models.Result

fun interface LogoutUseCase : suspend () -> Result<Unit>