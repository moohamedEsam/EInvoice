package com.example.domain.client

import com.example.models.Client
import kotlinx.coroutines.flow.Flow

fun interface GetClientsUseCase :  () -> Flow<List<Client>>