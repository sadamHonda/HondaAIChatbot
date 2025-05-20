# Honda Two-Wheeler AI Chatbot

This Android application implements a specialized AI chatbot focused on Honda two-wheeler products. It leverages Google's Gemini Pro API for conversational AI capabilities and is built using Kotlin and Jetpack Compose for the user interface. The chatbot is designed to understand and respond *exclusively in Japanese* to queries related to Honda's two-wheeler lineup.

## Screenshots 

![image](https://github.com/user-attachments/assets/2db9a738-3a45-4523-aa85-a7ce4612720c)

## Features

*   **Specialized AI:** The chatbot is configured with a system prompt to act as a Japanese AI customer specializing *exclusively* in Honda two-wheeler products.
*   **Japanese Language Only:** All responses from the AI are in Japanese.
*   **Chat Interface:** A simple and intuitive chat interface for users to ask questions.
*   **Message History:** The conversation history is maintained and sent to the AI for contextual responses.
*   **Real-time Updates:** The UI updates dynamically as messages are sent and received.
*   **"Typing..." Indicator:** Shows a "Typing..." message while waiting for the AI's response.
*   **Error Handling:** Basic error handling for API requests, displaying an error message to the user.
*   **Jetpack Compose UI:** The entire UI is built using modern Android declarative UI toolkit.

## Core Components

<img width="782" alt="Screenshot 2025-05-20 at 5 26 19 PM" src="https://github.com/user-attachments/assets/d30e6a39-2cdd-44ff-94af-c35b8cf3fb5e" />


The application is structured around several key components:

1.  **`ChatViewModel.kt`**: The ViewModel responsible for managing the chat state and business logic.
    *   `messageList`: A `mutableStateListOf<MessageModel>` that holds all messages in the chat. This being a `mutableStateListOf` ensures Compose UI updates when messages are added or removed.
    *   `initialSystemPrompt`: A predefined string that instructs the Gemini model on its persona and limitations (Honda two-wheelers, Japanese language only).
    *   `generativeModel`: An instance of `GenerativeModel` from the Google Generative AI SDK, configured with:
        *   `modelName`: "gemini-1.5-pro-latest"
        *   `apiKey`: Your Google AI Studio API key (should be stored in `Constants.apiKey`).
        *   `systemInstruction`: The `initialSystemPrompt` is passed here.
    *   `sendMessage(question: String)`:
        *   Launches a coroutine in `viewModelScope`.
        *   Constructs `chatHistory` from the current `messageList`.
        *   Adds the user's `question` and a temporary "Typing...." message to `messageList`.
        *   Starts a new chat session with `generativeModel.startChat(history = chatHistory)`.
        *   Sends the user's `question` to the chat session.
        *   Removes the "Typing...." message and adds the AI's `response.text`.
        *   Includes a `try-catch` block to handle exceptions during the API call, displaying an error message if one occurs.

2.  **UI Composables (likely in `ChatScreen.kt` or similar, or directly in `MainActivity.kt` as shown):**
    *   **`ChatPage(viewModel: ChatViewModel)`**: The main composable function for the chat screen.
        *   Arranges `AppHeader`, `MessageList`, and `MessageInput` in a `Column`.
    *   **`AppHeader()`**: A simple composable displaying the application title "Honda二輪車カスタマーサポート" (Honda Two-Wheeler Customer Support) in a styled `Box`.
    *   **`MessageList(messageList: List<MessageModel>)`**:
        *   Displays a welcome message and icon if `messageList` is empty.
        *   Uses a `LazyColumn` to efficiently display the list of messages in reverse order (latest at the bottom).
        *   Iterates through `messageList.reversed()` and calls `MessageRow` for each message.
    *   **`MessageRow(messageModel: MessageModel)`**:
        *   Displays a single message.
        *   Aligns user messages to the right and model (AI) messages to the left.
        *   Uses different background colors (`ColorUserMessage`, `ColorModelMessage`) for user and model messages.
        *   Wraps the message text in a `SelectionContainer` to allow users to copy the text.
    *   **`MessageInput(onMessageSend: (String) -> Unit)`**:
        *   Provides an `OutlinedTextField` for the user to type their message.
        *   Includes an `IconButton` with a "Send" icon to trigger the `onMessageSend` callback.
        *   Clears the input field after a message is sent.

3.  **`MessageModel.kt`**:
    *   A simple `data class` to represent a single chat message.
        *   `message: String`: The content of the message.
        *   `role: String`: The sender of the message ("user" or "model").

4.  **`MainActivity.kt`**:
    *   The main entry point of the application.
    *   Initializes `ChatViewModel` using `ViewModelProvider`.
    *   Sets up the main UI content using `AIChatbotTheme` and a `Scaffold`.
    *   Calls the `ChatPage` composable to display the chat interface.


## Setup and Configuration

1.  **API Key**:
    *   You need a Google AI Studio API key for the Gemini Pro model.
    *   This key should be placed in a `Constants.kt` file (or a similar configuration file) in your project:
        ```kotlin
        // Create a file named Constants.kt (e.g., in your main package)
        object Constants {
            const val apiKey = "YOUR_GEMINI_API_KEY"
        }
        ```
    *   **Important**: Add `Constants.kt` to your `.gitignore` file to avoid committing your API key to version control.

2.  **Dependencies**:
    *   Ensure you have the necessary Google Generative AI SDK dependency in your `build.gradle` (Module: app) file:
        ```gradle
        dependencies {
            // ... other dependencies
            implementation("com.google.ai.client.generativeai:generativeai:0.3.0") // Check for the latest version
        }
        ```
    *   Also, ensure you have internet permission in your `AndroidManifest.xml`:
        ```xml
        <uses-permission android:name="android.permission.INTERNET"/>
        ```
