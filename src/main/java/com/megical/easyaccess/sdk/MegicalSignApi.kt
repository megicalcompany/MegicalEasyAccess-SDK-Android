package com.megical.easyaccess.sdk

import android.net.Uri
import com.megical.easyaccess.sdk.api.SignApi
import com.megical.easyaccess.sdk.utils.Adapters
import com.squareup.moshi.Moshi
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.Callback as RetrofitCallback

private const val AUTH_BASE_URL = "https://auth-prod.megical.com/"

class MegicalSignApi {

    private val retrofit = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .connectionSpecs(
                    listOf(
                        ConnectionSpec.MODERN_TLS
                    )
                )
                .addNetworkInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .build()
        )
        .baseUrl(AUTH_BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi
                    .Builder()
                    .add(Adapters())
                    .build()
            )
        )
        .build()

    private val signApi: SignApi = retrofit.create(SignApi::class.java)

    /**
     * dataToSign can be plaintext or encoded with e.g. base64
     */
    fun initSign(
        authEnv: String,
        signatureEndpoint: String,
        dataToSign: String,
        callback: Callback<SigningData>
    ) {
        signApi.initSignData(
            signatureEndpoint,
            SignApi.InitSignRequest(dataToSign)
        ).enqueue(object : RetrofitCallback<SignApi.InitSignatureResponse> {

            override fun onResponse(
                call: Call<SignApi.InitSignatureResponse>,
                response: Response<SignApi.InitSignatureResponse>
            ) {
                response.takeIf { it.isSuccessful }?.body()?.let {
                    callback.onSuccess(
                        SigningData(
                            it.signatureCode,
                            Uri.parse("com.megical.easyaccess:/signature?signatureCode=${it.signatureCode}&authEnv=$authEnv")
                        )
                    )
                } ?: callback.onFailure(InvalidResponse())
            }

            override fun onFailure(call: Call<SignApi.InitSignatureResponse>, t: Throwable) {
                callback.onFailure(SigningError(t))
            }
        })
    }

}
