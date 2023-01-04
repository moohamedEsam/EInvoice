package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.IsUserLoggedInUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivityViewModel(private val isUserLoggedInUseCase: IsUserLoggedInUseCase) :
    ViewModel() {
    private val _userLoggedIn = MutableStateFlow<Boolean?>(null)
    val userLoggedIn = _userLoggedIn.asStateFlow()

    init {
        _userLoggedIn.update { isUserLoggedInUseCase() }
    }

}