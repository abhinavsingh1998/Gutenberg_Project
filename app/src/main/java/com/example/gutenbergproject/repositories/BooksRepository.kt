package com.example.gutenbergproject.repositories

import com.example.gutenbergproject.models.BookResponse
import com.example.gutenbergproject.retrofit.BookApi
import com.example.gutenbergproject.utility.ApiResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class BookRepository @Inject constructor(private val api: BookApi) {

    suspend fun searchBooks(query: String, page: Int?, limit: Int?): ApiResult<BookResponse> = try {
        val resp = api.searchBooks(query, mimeType = "image/jpeg", page, limit)
        if (resp.isSuccessful) {
            resp.body()?.let { ApiResult.Success(it) } ?: ApiResult.Error("No data found")
        } else {
            ApiResult.Error("Error ${resp.code()}: ${resp.message()}")
        }
    } catch (e: IOException) {
        ApiResult.Error("Network error: ${e.localizedMessage ?: "Unknown"}")
    } catch (e: HttpException) {
        ApiResult.Error("Server error: ${e.code()}")
    }
}
