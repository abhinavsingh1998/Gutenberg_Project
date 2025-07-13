package com.example.gutenbergproject.retrofit

import com.example.gutenbergproject.models.BookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {
    @GET("books/")
    suspend fun searchBooks(
        @Query("search") query: String,
        @Query("mime_type") mimeType: String = "mage/jpeg",
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BookResponse>
}
