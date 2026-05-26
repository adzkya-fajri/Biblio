package com.example.biblio.data.remote.apis

import retrofit2.http.*
import retrofit2.Call

import com.example.biblio.data.remote.dto.PaymentSubscribeResponse

interface PaymentApi {
    /**
     * POST payment/subscribe
     * Buat transaksi langganan, return Snap token
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthenticated
     *
     * @return [Call]<[PaymentSubscribeResponse]>
     */
    @POST("payment/subscribe")
    fun createSubscription(): Call<PaymentSubscribeResponse>
}
