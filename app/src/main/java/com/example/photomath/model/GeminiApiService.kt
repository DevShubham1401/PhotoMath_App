package com.example.photomath.model
//
//import retrofit2.http.Body
//import retrofit2.http.POST
//import retrofit2.http.Query
//
//interface GeminiApiService {
//    @POST("v1beta/models/gemini-pro:generateContent")
//    suspend fun solveEquation(
//        @Query("key") apiKey: String,  // ✅ Fix: API Key as Query Parameter
//        @Body request: SolveEquationRequest
//    ): SolveEquationResponse
//}

//
//package com.example.photomath.model
//
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.POST
//import retrofit2.http.Query
//
//interface GeminiApiService {
//    @POST("v1/models/gemini-pro-vision:generateContent")
//    suspend fun sendImageToGemini(
//        @Query("key") apiKey: String,
//        @Body requestBody: Map<String, Any>
//    ): Response<SolveEquationResponse>
//}

import retrofit2.http.GET
import retrofit2.http.Query
interface GeminiApiService {
    @GET("solveEquation") // Adjust endpoint as needed
    suspend fun solveEquation(@Query("equation") equation: String): SolveEquationResponse
}
