package com.costular.jellydroid.data.error

sealed interface JellydroidError {
    data object ConnectionError : JellydroidError
    data object UnknownError : JellydroidError
}