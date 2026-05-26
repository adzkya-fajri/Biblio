package com.example.biblio.data.remote.apis

import retrofit2.http.*
import retrofit2.Call

import com.example.biblio.data.remote.dto.ApiMessageResponse
import com.example.biblio.data.remote.dto.Readlist
import com.example.biblio.data.remote.dto.ReadlistBookRequest
import com.example.biblio.data.remote.dto.ReadlistDetail
import com.example.biblio.data.remote.dto.ReadlistRequest
import com.example.biblio.data.remote.dto.UpdateReadlistRequest

interface ReadlistsApi {
    /**
     * POST readlists/{id}/books
     * Tambah buku ke readlist
     * 
     * Responses:
     *  - 201: Readlist dibuat
     *  - 403: Forbidden
     *  - 422: Buku tidak valid
     *  - 401: Unauthenticated
     *
     * @param id 
     * @param readlistBookRequest 
     * @return [Call]<[ApiMessageResponse]>
     */
    @POST("readlists/{id}/books")
    fun addBookToReadlist(@Path("id") id: java.util.UUID, @Body readlistBookRequest: ReadlistBookRequest): Call<ApiMessageResponse>

    /**
     * DELETE readlists/{id}/books
     * Hapus buku dari readlist
     * 
     * Responses:
     *  - 204: Buku dihapus
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 422: Buku tidak valid
     *  - 401: Unauthenticated
     *
     * @param id 
     * @param readlistBookRequest 
     * @return [Call]<[Unit]>
     */
    @DELETE("readlists/{id}/books")
    fun deleteBookFromReadlist(@Path("id") id: java.util.UUID, @Body readlistBookRequest: ReadlistBookRequest): Call<Unit>

    /**
     * DELETE readlists/{id}
     * Hapus readlist
     * 
     * Responses:
     *  - 204: Readlist dihapus
     *  - 403: Forbidden
     *  - 401: Unauthenticated
     *
     * @param id 
     * @return [Call]<[Unit]>
     */
    @DELETE("readlists/{id}")
    fun deleteReadlist(@Path("id") id: java.util.UUID): Call<Unit>

    /**
     * GET readlists/{id}
     * Detail readlist
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *  - 403: Forbidden
     *
     * @param id 
     * @return [Call]<[ReadlistDetail]>
     */
    @GET("readlists/{id}")
    fun getReadlist(@Path("id") id: java.util.UUID): Call<ReadlistDetail>

    /**
     * GET readlists
     * List readlist milik user
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *
     * @return [Call]<[kotlin.collections.List<ReadlistDetail>]>
     */
    @GET("readlists")
    fun getReadlists(): Call<kotlin.collections.List<ReadlistDetail>>

    /**
     * POST readlists
     * Buat readlist baru
     * 
     * Responses:
     *  - 201: Readlist dibuat
     *  - 401: Unauthenticated
     *
     * @param readlistRequest 
     * @return [Call]<[Readlist]>
     */
    @POST("readlists")
    fun postReadlist(@Body readlistRequest: ReadlistRequest): Call<Readlist>

    /**
     * PUT readlists/{id}
     * Update readlist
     * 
     * Responses:
     *  - 201: Readlist dibuat
     *  - 403: Forbidden
     *  - 401: Unauthenticated
     *
     * @param id 
     * @param updateReadlistRequest 
     * @return [Call]<[Readlist]>
     */
    @PUT("readlists/{id}")
    fun updateReadlist(@Path("id") id: java.util.UUID, @Body updateReadlistRequest: UpdateReadlistRequest): Call<Readlist>

}
