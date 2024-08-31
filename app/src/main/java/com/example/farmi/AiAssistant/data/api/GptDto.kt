package com.example.farmi.AiAssistant.data.api


import com.example.farmi.AiAssistant.data.response.GptResponse

interface GptDto {
    suspend fun getmessage(openAIRequest: OpenAIRequest): GptResponse
}