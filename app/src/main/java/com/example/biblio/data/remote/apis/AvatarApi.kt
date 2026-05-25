package com.example.biblio.data.remote.apis

import com.example.biblio.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.biblio.models.ApiMessageResponse
import com.example.biblio.models.AvatarResponse
import com.example.biblio.models.ValidationErrorResponse

import okhttp3.MultipartBody

interface AvatarApi {
    /**
     * DELETE profile/avatar
     * Hapus avatar user
     * 
     * Responses:
     *  - 200: Berhasil
     *  - 401: Unauthenticated
     *
     * @return [Call]<[ApiMessageResponse]>
     */
    @DELETE("profile/avatar")
    fun deleteAvatar(): Call<ApiMessageResponse>

    /**
     * POST profile/avatar
     * Upload avatar user
     * 
     * Responses:
     *  - 200: Berhasil
     *  - 401: Unauthenticated
     *  - 422: File tidak valid
     *
     * @param avatar 
     * @return [Call]<[AvatarResponse]>
     */
    @Multipart
    @POST("profile/avatar")
    fun uploadAvatar(@Part avatar: MultipartBody.Part): Call<AvatarResponse>

}
