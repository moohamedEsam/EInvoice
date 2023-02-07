package com.example.models.branch

import com.example.models.item.Item

data class BranchView(
    val branch: Branch,
    val items: List<Item>,
)
