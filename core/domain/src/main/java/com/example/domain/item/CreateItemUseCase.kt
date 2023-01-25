package com.example.domain.item

import com.example.common.models.Result
import com.example.models.item.Item

fun interface CreateItemUseCase : suspend (Item) -> Result<Item>