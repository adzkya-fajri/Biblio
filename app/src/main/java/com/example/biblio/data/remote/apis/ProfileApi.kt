package com.example.biblio.data.remote.apis

import retrofit2.http.*
import retrofit2.Call

import com.example.biblio.data.remote.dto.ApiMessageResponse
import com.example.biblio.data.remote.dto.AvatarResponse
import com.example.biblio.data.remote.dto.User

import okhttp3.MultipartBody

interface ProfileApi {
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
     * GET profile
     * Get profil user yang sedang login
     *
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *
     * @return [Call]<[User]>
     */
    @GET("profile")
    fun getProfile(): Call<User>

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
