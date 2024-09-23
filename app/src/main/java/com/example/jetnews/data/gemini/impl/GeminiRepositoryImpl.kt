package com.example.jetnews.data.gemini.impl

import com.example.jetnews.data.gemini.GeminiRepository
import com.example.jetnews.model.Post
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.BlockThreshold
import com.google.firebase.vertexai.type.HarmCategory
import com.google.firebase.vertexai.type.SafetySetting
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI

class GeminiRepositoryImpl: GeminiRepository {


    private val generativeModel = Firebase
        .vertexAI
        .generativeModel(
            modelName = "gemini-1.5-flash",
            generationConfig = generationConfig {
                temperature = 0f
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.LOW_AND_ABOVE),
            )
        )

    override suspend fun summarizePost(post: Post): String? {
        val stringBuilder = StringBuilder()
        post.paragraphs.forEach {
            stringBuilder.append(it.text)
        }

        val prompt =
            "Summarize the following article in 4 concise bullet points. " +
                    "Ensure each bullet point is specific, informative and relevant. " +
                    "Return just the bullet points as plain text. " +
                    "Use plain text, don't use markdown. \n $stringBuilder"

        return generativeModel.generateContent(prompt).text
    }

}