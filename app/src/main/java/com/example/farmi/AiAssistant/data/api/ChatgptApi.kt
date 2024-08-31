package com.example.farmi.AiAssistant.data.api

import com.example.farmi.AiAssistant.data.response.GptResponse
import retrofit2.http.Body
import retrofit2.http.POST

data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo-instruct",
    var prompt: String ,
    val max_tokens: Int = 4,
    val temperature: Float =0f
)

interface ChatgptApi {

    @POST("v1/completions")
    suspend fun getmessage(

        @Body request: OpenAIRequest
    ): GptResponse
}