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
    Cancelled
}