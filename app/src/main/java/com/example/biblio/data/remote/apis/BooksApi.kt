package com.example.biblio.data.remote.apis

import retrofit2.http.*
import retrofit2.Call

import com.example.biblio.data.remote.dto.ApiMessageResponse
import com.example.biblio.data.remote.dto.Book
import com.example.biblio.data.remote.dto.BookDownloadResponse
import com.example.biblio.data.remote.dto.BookPaginatedResponse
import com.squareup.moshi.Json

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
     * enum for parameter format
     */
    enum class FormatGetBooks(val value: kotlin.String) {
        @Json(name = "pdf") pdf("pdf"),
        @Json(name = "epub") epub("epub"),
        @Json(name = "mobi") mobi("mobi"),
        @Json(name = "djvu") djvu("djvu")
    }

    /**
     * enum for parameter sort
     */
    enum class SortGetBooks(val value: kotlin.String) {
        @Json(name = "latest") latest("latest"),
        @Json(name = "oldest") oldest("oldest"),
        @Json(name = "price_asc") price_asc("price_asc"),
        @Json(name = "price_desc") price_desc("price_desc")
    }

    /**
     * GET books
     * List semua buku (paginated)
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *
     * @param page  (optional)
     * @param search  (optional)
     * @param genreId  (optional)
     * @param format  (optional)
     * @param sort  (optional)
     * @return [Call]<[BookPaginatedResponse]>
     */
    @GET("books")
    fun getBooks(@Query("page") page: kotlin.Int? = null, @Query("search") search: kotlin.String? = null, @Query("genre_id") genreId: kotlin.Int? = null, @Query("format") format: FormatGetBooks? = null, @Query("sort") sort: SortGetBooks? = null): Call<BookPaginatedResponse>

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
