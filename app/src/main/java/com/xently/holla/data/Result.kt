package com.xently.holla.data

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> this.data.toString()
            is Error -> this.error.toString()
            Loading -> "Loading..."
        }
    }
}

inline val Result<*>.isLoading
    get() = this is Result.Loading

inline val Result<*>.isSuccessful
    get() = this is Result.Success && data != null

inline val <T> Result<T>.data: T?
    get() = if (isSuccessful) (this as Result.Success).data else null

inline val <T> Result<T>.dataOrFail: T
    get() = try {
        this.data!!
    } catch (ex: Exception) {
        throw Exception("Invalid ${Result::class.java.simpleName} type! ${ex.message}")
    }

inline val <T> Result<List<T>>.listData: List<T>
    get() = data ?: emptyList()

inline val Result<*>.isError
    get() = this is Error

inline val <T> Result<T>.errorMessage: String?
    get() = if (isError) (this as Result.Error).message else null

inline val Result.Error.message: String
    get() = error.message!!