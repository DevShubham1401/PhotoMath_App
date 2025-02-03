package com.example.photomath.model

import retrofit2.http.GET
import retrofit2.http.Query

interface GeminiApiService {
    @GET("solve") // Change endpoint as per API docs
    suspend fun solveEquation(@Query("equation") equation: String): SolveEquationResponse
}
