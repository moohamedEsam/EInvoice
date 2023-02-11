package com.example.models.branch

import com.example.models.company.Company
import com.example.models.item.Item

data class BranchView(
    val branch: Branch,
    val items: List<Item>,
    val company: Company,
)
