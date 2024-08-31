package com.example.farmi.AiAssistant.data.response

data class GptResponse(
    val choices: List<Choice>?,
    val created: Long?,
    val id: String?,
    val model: String?,
    val `object`: String?,
    val usage: Usage?
)

data class Choice(
    val finish_reason: String?,
    val index: Int?,
    val logprobs: Any?,  // This can be further specified based on the actual data type
    val text: String?
)

data class Usage(
    val completion_tokens: Int?,
    val prompt_tokens: Int?,
    val total_tokens: Int?
)