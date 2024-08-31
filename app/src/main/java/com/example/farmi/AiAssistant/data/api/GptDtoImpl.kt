package com.example.farmi.AiAssistant.data.api


import android.util.Log
import com.example.farmi.AiAssistant.data.response.GptResponse
import javax.inject.Inject


class GptDtoImpl @Inject constructor(
    private val api: ChatgptApi
) : GptDto {
    override suspend fun getmessage(openAIRequest: OpenAIRequest): GptResponse {
        Log.d("body","body of the message ${openAIRequest.prompt}")
        return api.getmessage(
            request =  openAIRequest

        )
    }
}
