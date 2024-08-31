package com.example.farmi.AiAssistant.viewmodel


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.AiAssistant.data.api.OpenAIRequest
import com.example.farmi.AiAssistant.data.response.GptResponse
import com.example.farmi.AiAssistant.repository.GptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val gptRepository: GptRepository
) : ViewModel() {

    private val _outputText = mutableStateOf("")
    val outputText: State<String> = _outputText

    var ankit: MutableState<String> = mutableStateOf("ankit")

    val chats: MutableList<String> = mutableStateListOf("ankit")

    private val _chatHistory = mutableStateListOf<String>()
    val chatHistory: SnapshotStateList<String> = _chatHistory



    init {
        _chatHistory.add("Hi! I am your AI Assistance How Can I help you.😊")
//        _chatHistory.add("helloooooo00000 skdjaksdad adk ")


    }
    private val _isError = mutableStateOf(false)
    val isError: State<Boolean> = _isError


    fun addInChat(inputText: String) {
        chats.add(inputText)
        _chatHistory.add(inputText)

    }


    fun fetchAndProcessText(inputText: String) {
        val prompt = "Answer only if questions related to agriculture field if the question is not related to agriculture field write as your query is not related to agriculture field Question: ${inputText} "

        ankit.value = "khyalia"
        addInChat(inputText)
        viewModelScope.launch {
            try {
                val response = gptRepository.fetchData(
                    OpenAIRequest(
                        model = "gpt-3.5-turbo-instruct",
                        prompt = prompt,
                        max_tokens = 400,
                        temperature = 0f
                    )

                )

                // Process the response and update the state
                processResponse(response)
            } catch (e: Exception) {
                // Handle errors
                _isError.value = true
            }
        }
    }

    private fun processResponse(response: GptResponse) {
        // Update the outputText state with the text from choices
        _outputText.value =
            (response.choices?.joinToString("\n") { it.text.toString() } as CharSequence?
                ?: "No response").toString()
        addInChat(_outputText.value)
    }
}
