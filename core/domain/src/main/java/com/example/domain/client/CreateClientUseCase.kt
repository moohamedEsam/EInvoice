package com.example.domain.client

import com.example.common.models.Result
import com.example.models.Client

fun interface CreateClientUseCase : suspend (Client) -> Result<Client>