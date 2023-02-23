package com.example.domain.document

import androidx.paging.PagingSource
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.Flow

fun interface GetDocumentsPagingSourceUseCase : () -> PagingSource<Int, DocumentView>