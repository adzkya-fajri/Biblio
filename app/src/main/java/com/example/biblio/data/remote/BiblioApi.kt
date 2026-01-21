package com.example.biblio.data.remote

import com.example.biblio.data.remote.dto.AuthResponse
import com.example.biblio.data.remote.dto.BookDto
import com.example.biblio.data.remote.dto.DownloadResponse
import com.example.biblio.data.remote.dto.GenreWithBooksDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BiblioApi {

    @POST("auth/firebase")
    suspend fun firebaseLogin(@Body body: Map<String, String>): AuthResponse

    @GET("genres/with-books")
    suspend fun getGenresWithBooks(): List<GenreWithBooksDto>

    @GET("books/{id}")
    suspend fun getBook(@Path("id") id: String): BookDto

    @GET("books/{id}/download")
    suspend fun getDownloadUrl(
        @Path("id") id: String,
        @Query("preview") preview: Boolean = false
    ): DownloadResponse
}
