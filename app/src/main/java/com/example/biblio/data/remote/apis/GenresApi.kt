package com.example.biblio.data.remote.apis

import retrofit2.http.*
import retrofit2.Call

import com.example.biblio.data.remote.dto.Genre
import com.example.biblio.data.remote.dto.GenreWithBooksResponse

interface GenresApi {
    /**
     * GET genres/{id}
     * Detail genre
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *  - 404: Not Found
     *
     * @param id 
     * @return [Call]<[Genre]>
     */
    @GET("genres/{id}")
    fun getGenre(@Path("id") id: kotlin.Int): Call<Genre>

    /**
     * GET genres/with-books
     * List genre beserta preview buku (untuk beranda)
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *
     * @return [Call]<[kotlin.collections.List<GenreWithBooksResponse>]>
     */
    @GET("genres/with-books")
    fun getGenreWithBooks(): Call<kotlin.collections.List<GenreWithBooksResponse>>

    /**
     * GET genres
     * List semua genre
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *
     * @return [Call]<[kotlin.collections.List<Genre>]>
     */
    @GET("genres")
    fun getGenres(): Call<kotlin.collections.List<Genre>>

}
