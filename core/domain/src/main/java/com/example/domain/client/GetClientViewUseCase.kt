package com.example.domain.client

import com.example.models.Client
import com.example.models.ClientView
import kotlinx.coroutines.flow.Flow

fun interface GetClientViewUseCase :  (String) -> Flow<ClientView>