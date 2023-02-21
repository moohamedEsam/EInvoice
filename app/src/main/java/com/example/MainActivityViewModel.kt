package com.example

import androidx.lifecycle.ViewModel
import com.example.domain.auth.IsUserLoggedInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainActivityViewModel(private val isUserLoggedInUseCase: IsUserLoggedInUseCase) :
    ViewModel() {
    private val _userLoggedIn = MutableStateFlow<Boolean?>(null)
    val userLoggedIn = _userLoggedIn.asStateFlow()

    init {
        _userLoggedIn.update { isUserLoggedInUseCase() }
    }

}