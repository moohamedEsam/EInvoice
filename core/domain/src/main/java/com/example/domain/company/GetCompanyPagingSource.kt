package com.example.domain.company

import androidx.paging.PagingSource
import com.example.models.company.CompanyView

fun interface GetCompanyPagingSource : () -> PagingSource<Int, CompanyView>