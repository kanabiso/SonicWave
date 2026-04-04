# SonicWave 🎵

SonicWave is a local music player for Android. Built with a 100% Kotlin codebase and the latest Android development practices, it allows users to seamlessly browse and play audio files stored directly on their device.

## 📱 Screenshots



## ✨ Features

* **Reactive Device Search**: Instantly find your favorite songs with real-time, reactive searching powered by Kotlin Flows.
* **Album Browsing**: Automatically groups your music into albums. Tapping on an album displays its complete, dedicated tracklist.
* **Customizable Layouts**: Switch effortlessly between **List** and **Grid** views for both your tracks and albums.
* **Advanced Sorting**: Easily sort your music library to find exactly what you're looking for.
* **Background Playback**: Reliable audio playback and media controls utilizing modern Android media APIs.

## 🛠️ Tech Stack & Architecture

SonicWave is built using modern Android development tools and libraries:

* **[Kotlin](https://kotlinlang.org/)**: The entire application is written in Kotlin.
* **[Jetpack Compose](https://developer.android.com/jetpack/compose)**: Android's modern toolkit for building native UI in a declarative way.
* **[Hilt](https://dagger.dev/hilt/)**: Dependency injection framework to manage application components and lifecycles.
* **[Media3](https://developer.android.com/guide/topics/media/media3)**: The latest iteration of Android's media playback library, providing robust audio handling and player architecture.
* **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)**: For navigating between different screens and components within the Compose ecosystem.
* **Media Store & Content Resolver**: Used to securely and efficiently query local audio files and album metadata stored on the user's device.
* **Coroutines & Flow**: Used for handling asynchronous tasks and providing reactive data streams directly to the UI.

