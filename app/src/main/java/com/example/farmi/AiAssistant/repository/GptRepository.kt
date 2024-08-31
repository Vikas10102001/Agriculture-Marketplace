package com.example.farmi.AiAssistant.repository



import com.example.farmi.AiAssistant.data.api.GptDto
import com.example.farmi.AiAssistant.data.api.OpenAIRequest
import com.example.farmi.AiAssistant.data.response.GptResponse
import javax.inject.Inject

class GptRepository @Inject constructor(
    private  val gptDto: GptDto
) {
    suspend fun fetchData(openAIRequest: OpenAIRequest): GptResponse {
        return gptDto.getmessage(openAIRequest)
    }
}