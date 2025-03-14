package com.costular.jellydroid.data.error

sealed interface AddServerError : JellydroidError {
    data object NotFound : AddServerError
}