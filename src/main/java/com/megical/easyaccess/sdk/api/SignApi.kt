package com.megical.easyaccess.sdk.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

internal interface SignApi {
    
    @POST
    fun initSignData(
        @Url url: String,
        @Body initSignRequest: InitSignRequest
    ) : Call<InitSignatureResponse>
    
    @JsonClass(generateAdapter = true)
    data class InitSignRequest(
        @Json(name = "signData") val signData: String
    )
    
    @JsonClass(generateAdapter = true)
    data class InitSignatureResponse(
        @Json(name = "signatureCode") val signatureCode: String
    )
    
}