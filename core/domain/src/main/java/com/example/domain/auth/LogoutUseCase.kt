package com.example.domain.auth

import com.example.common.models.Result

fun interface LogoutUseCase : suspend () -> Result<Unit>