package com.example.jetnews.data.gemini

import com.example.jetnews.model.Post

interface GeminiRepository {

    suspend fun summarizePost(post: Post): String?
}