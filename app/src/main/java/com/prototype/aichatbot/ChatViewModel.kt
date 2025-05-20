package com.prototype.aichatbot
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    private val initialSystemPrompt = """
        You are a Japanese AI customer specializing *exclusively* in the Honda two wheelers product. 
        Only answer *in Japanese* questions related to Honda products.
""".trimIndent()

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro-latest",
        apiKey = Constants.apiKey ,systemInstruction = content { text(initialSystemPrompt) }
    )

    fun sendMessage(question : String){
        viewModelScope.launch {

            try{


                val chatHistory = messageList.map {
                    content(it.role) { text(it.message) }
                }.toList()

                messageList.add(MessageModel(question,"user"))
                messageList.add(MessageModel("Typing....","model"))

                val chat = generativeModel.startChat(
                    history = chatHistory,
                )

                val response = chat.sendMessage(question)

                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(),"model"))
            }catch (e : Exception){
                messageList.removeLast()
                messageList.add(MessageModel("Error : "+e.message.toString(),"model"))
            }


        }
    }
}