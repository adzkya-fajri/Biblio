package com.example.biblio.data.remote.apis

import com.example.biblio.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.biblio.models.ApiMessageResponse
import com.example.biblio.models.FirebaseLogin
import com.example.biblio.models.UserCredentialsResponse
import com.example.biblio.models.ValidationErrorResponse
import retrofit2.Response

interface AuthApi {
    /**
     * POST auth/firebase
     * Login user via Firebase token
     * 
     * Responses:
     *  - 200: Login berhasil
     *  - 422: Token tidak valid
     *
     * @param firebaseLogin 
     * @return [Call]<[UserCredentialsResponse]>
     */
    @POST("auth/firebase")
    suspend fun authFirebase(@Body firebaseLogin: FirebaseLogin): Response<UserCredentialsResponse>

    /**
     * POST auth/logout
     * Logout
     * 
     * Responses:
     *  - 200: Logged out
     *  - 401: Unauthenticated
     *
     * @return [Call]<[ApiMessageResponse]>
     */
    @POST("auth/logout")
    suspend fun authLogout(): Response<ApiMessageResponse>

}
