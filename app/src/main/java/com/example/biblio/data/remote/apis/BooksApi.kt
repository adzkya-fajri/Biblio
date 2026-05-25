package com.example.biblio.apis

import com.example.biblio.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.biblio.models.ApiMessageResponse
import com.example.biblio.models.Book
import com.example.biblio.models.BookDownloadResponse
import com.example.biblio.models.BookPaginatedResponse

interface BooksApi {
    /**
     * GET books/{id}/download
     * Dapatkan temporary URL download file buku
     * 
     * Responses:
     *  - 200: OK
     *  - 404: Not Found
     *  - 401: Unauthenticated
     *
     * @param id 
     * @return [Call]<[BookDownloadResponse]>
     */
    @GET("books/{id}/download")
    fun downloadBook(@Path("id") id: java.util.UUID): Call<BookDownloadResponse>

    /**
     * GET books/{id}
     * Detail buku
     * 
     * Responses:
     *  - 200: OK
     *  - 404: Not Found
     *  - 401: Unauthenticated
     *
     * @param id 
     * @return [Call]<[Book]>
     */
    @GET("books/{id}")
    fun getBook(@Path("id") id: java.util.UUID): Call<Book>

    /**
     * GET books
     * List semua buku (paginated)
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *
     * @param page  (optional)
     * @return [Call]<[BookPaginatedResponse]>
     */
    @GET("books")
    fun getBooks(@Query("page") page: kotlin.Int? = null): Call<BookPaginatedResponse>

    /**
     * GET genres/{id}/books
     * List buku berdasarkan genre (paginated)
     * 
     * Responses:
     *  - 200: OK
     *  - 404: Not Found
     *  - 401: Unauthenticated
     *
     * @param id 
     * @param page  (optional)
     * @return [Call]<[BookPaginatedResponse]>
     */
    @GET("genres/{id}/books")
    fun getBooksByGenre(@Path("id") id: kotlin.Int, @Query("page") page: kotlin.Int? = null): Call<BookPaginatedResponse>

}
