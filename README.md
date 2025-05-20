# Honda二輪車AIチャットボット

このAndroidアプリケーションは、Hondaの二輪車製品に特化したAIチャットボットを実装しています。会話型AI機能にはGoogleのGemini Pro APIを活用し、ユーザーインターフェースはKotlinとJetpack Composeを使用して構築されています。チャットボットは、Hondaの二輪車製品ラインナップに関する問い合わせに対し、*日本語のみ*で理解し応答するように設計されています。

## スクリーンショット

![image](https://github.com/user-attachments/assets/2db9a738-3a45-4523-aa85-a7ce4612720c)

## 機能

*   **特化型AI:** チャットボットは、Hondaの二輪車製品に*特化*した日本のAIカスタマーとして機能するよう、システムプロンプトで設定されています。
*   **日本語のみ:** AIからのすべての応答は日本語で行われます。
*   **チャットインターフェース:** ユーザーが質問をするためのシンプルで直感的なチャットインターフェースです。
*   **メッセージ履歴:** 会話履歴は維持され、文脈に応じた応答のためにAIに送信されます。
*   **リアルタイム更新:** メッセージの送受信に応じてUIが動的に更新されます。
*   **「入力中...」インジケーター:** AIの応答を待っている間、「入力中...」というメッセージが表示されます。
*   **エラー処理:** APIリクエストの基本的なエラー処理を行い、ユーザーにエラーメッセージを表示します。
*   **Jetpack Compose UI:** UI全体が最新のAndroid宣言的UIツールキットを使用して構築されています。

## 主要コンポーネント

<img width="782" alt="Screenshot 2025-05-20 at 5 26 19 PM" src="https://github.com/user-attachments/assets/d30e6a39-2cdd-44ff-94af-c35b8cf3fb5e" />

このアプリケーションは、いくつかの主要コンポーネントを中心に構成されています。

1.  **`ChatViewModel.kt`**: チャットの状態とビジネスロジックを管理するViewModelです。
    *   `messageList`: チャット内のすべてのメッセージを保持する`mutableStateListOf<MessageModel>`です。`mutableStateListOf`であるため、メッセージが追加または削除されるとCompose UIが更新されます。
    *   `initialSystemPrompt`: Geminiモデルにそのペルソナと制約（Honda二輪車、日本語のみ）を指示する事前定義された文字列です。
    *   `generativeModel`: Google Generative AI SDKの`GenerativeModel`インスタンスで、以下のように設定されています。
        *   `modelName`: "gemini-1.5-pro-latest"
        *   `apiKey`: あなたのGoogle AI Studio APIキー（`Constants.apiKey`に保存する必要があります）。
        *   `systemInstruction`: ここに`initialSystemPrompt`が渡されます。
    *   `sendMessage(question: String)`:
        *   `viewModelScope`内でコルーチンを起動します。
        *   現在の`messageList`から`chatHistory`を構築します。
        *   ユーザーの`question`と一時的な「入力中...」メッセージを`messageList`に追加します。
        *   `generativeModel.startChat(history = chatHistory)`で新しいチャットセッションを開始します。
        *   ユーザーの`question`をチャットセッションに送信します。
        *   「入力中...」メッセージを削除し、AIの`response.text`を追加します。
        *   API呼び出し中の例外を処理するための`try-catch`ブロックを含み、エラーが発生した場合はエラーメッセージを表示します。

2.  **UIコンポーザブル (通常 `ChatScreen.kt` のようなファイル、または示されているように `MainActivity.kt` に直接記述):**
    *   **`ChatPage(viewModel: ChatViewModel)`**: チャット画面のメインとなるコンポーザブル関数です。
        *   `AppHeader`、`MessageList`、`MessageInput`を`Column`内に配置します。
    *   **`AppHeader()`**: アプリケーションのタイトル「Honda二輪車カスタマーサポート」をスタイル付きの`Box`に表示するシンプルなコンポーザブルです。
    *   **`MessageList(messageList: List<MessageModel>)`**:
        *   `messageList`が空の場合、ウェルカムメッセージとアイコンを表示します。
        *   `LazyColumn`を使用してメッセージのリストを逆順（最新のものが一番下）で効率的に表示します。
        *   `messageList.reversed()`を反復処理し、各メッセージに対して`MessageRow`を呼び出します。
    *   **`MessageRow(messageModel: MessageModel)`**:
        *   単一のメッセージを表示します。
        *   ユーザーメッセージを右に、モデル（AI）メッセージを左に揃えます。
        *   ユーザーとモデルのメッセージに異なる背景色（`ColorUserMessage`, `ColorModelMessage`）を使用します。
        *   メッセージテキストを`SelectionContainer`でラップし、ユーザーがテキストをコピーできるようにします。
    *   **`MessageInput(onMessageSend: (String) -> Unit)`**:
        *   ユーザーがメッセージを入力するための`OutlinedTextField`を提供します。
        *   `onMessageSend`コールバックをトリガーするための「送信」アイコンが付いた`IconButton`を含みます。
        *   メッセージ送信後に入力フィールドをクリアします。

3.  **`MessageModel.kt`**:
    *   単一のチャットメッセージを表すシンプルな`data class`です。
        *   `message: String`: メッセージの内容。
        *   `role: String`: メッセージの送信者（"user" または "model"）。

4.  **`MainActivity.kt`**:
    *   アプリケーションのメインエントリーポイントです。
    *   `ViewModelProvider`を使用して`ChatViewModel`を初期化します。
    *   `AIChatbotTheme`と`Scaffold`を使用してメインUIコンテンツを設定します。
    *   `ChatPage`コンポーザブルを呼び出してチャットインターフェースを表示します。

## セットアップと設定

1.  **APIキー**:
    *   Gemini Proモデル用のGoogle AI Studio APIキーが必要です。
    *   このキーは、プロジェクト内の`Constants.kt`ファイル（または同様の設定ファイル）に配置する必要があります。
        ```kotlin
        // Constants.ktという名前のファイルを作成します（例：メインパッケージ内）
        object Constants {
            const val apiKey = "YOUR_GEMINI_API_KEY" // ここにあなたのAPIキーを入力
        }
        ```
    *   **重要**: APIキーをバージョン管理にコミットしないように、`Constants.kt`を`.gitignore`ファイルに追加してください。

2.  **依存関係**:
    *   必要なGoogle Generative AI SDKの依存関係が`build.gradle`（モジュール: app）ファイルにあることを確認してください。
        ```gradle
        dependencies {
            // ... 他の依存関係
            implementation("com.google.ai.client.generativeai:generativeai:0.3.0") // 最新バージョンを確認してください
        }
        ```
    *   また、`AndroidManifest.xml`にインターネット権限があることを確認してください。
        ```xml
        <uses-permission android:name="android.permission.INTERNET"/>
        ```
