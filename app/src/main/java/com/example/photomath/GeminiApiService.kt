package com.example.photomath

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApiService {
    @POST("solve")
    fun solveEquation(@Body equation: String): Call<GeminiResponse>
}

data class GeminiResponse(
    val solution: String
)