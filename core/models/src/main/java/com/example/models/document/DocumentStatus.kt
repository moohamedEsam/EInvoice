package com.example.models.document



enum class DocumentStatus {
    Initial,
    Updated,
    SignError,
    Signed,
    InvalidSent,
    Submitted,
    Valid,
    Invalid,
    Rejected,
    Cancelled;

    fun isSendable() = when (this) {
        Initial, Updated, SignError, Signed, InvalidSent -> true
        else -> false
    }

    fun isUpdatable() = isSendable() || this == Invalid

    fun isCancelable() = when (this) {
        Submitted, Valid -> true
        else -> false
    }
}