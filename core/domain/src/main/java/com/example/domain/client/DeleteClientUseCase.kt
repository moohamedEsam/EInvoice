package com.example.domain.client

import com.example.common.models.Result

fun interface DeleteClientUseCase : suspend (String) -> Result<Unit>