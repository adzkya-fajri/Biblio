package com.example.biblio.data.remote.apis

import com.example.biblio.data.remote.dto.User
import retrofit2.http.*
import retrofit2.Call

import com.example.biblio.data.remote.dto.UpsertProgressRequest

interface ProgressApi {
    /**
     * DELETE progress
     * Hapus progress baca
     * 
     * Responses:
     *  - 204: Deleted
     *  - 401: Unauthenticated
     *
     * @param bookId
     * @return [Call]<[Unit]>
     */
    @DELETE("progress")
    fun deleteProgress(@Path("book_id") bookId: java.util.UUID): Call<Unit>

    /**
     * POST progress
     * Simpan atau update progress baca
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *  - 422: Validation error
     *
     * @param upsertProgressRequest
     * @return [Call]<[User]>
     */
    @POST("progress")
    fun upsertProgress(@Body upsertProgressRequest: UpsertProgressRequest): Call<User>

}
