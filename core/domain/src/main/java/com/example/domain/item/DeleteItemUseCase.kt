package com.example.domain.item

import com.example.common.models.Result

fun interface DeleteItemUseCase : suspend (String) -> Result<Unit>