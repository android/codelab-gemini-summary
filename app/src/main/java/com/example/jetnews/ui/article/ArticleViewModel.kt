package com.example.jetnews.ui.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.data.gemini.GeminiRepository
import com.example.jetnews.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed interface SummarizationState {

    data object None: SummarizationState

    data object Loading: SummarizationState

    data class Loaded(val summary:String): SummarizationState

    data class Error(val error: String): SummarizationState

}

class ArticleViewModel(
    val post: Post,
    private val geminiRepository: GeminiRepository
): ViewModel() {

    private val _summarizationState = MutableStateFlow<SummarizationState>(SummarizationState.None)
    val summarizationState = _summarizationState

    fun summarizePost() {
        viewModelScope.launch {
            summarizationState.emit(SummarizationState.Loading)

            try {
                val result = geminiRepository.summarizePost(post)

                if (result!=null) {
                    summarizationState.emit(SummarizationState.Loaded(result))
                } else {
                    summarizationState.emit(SummarizationState.Error("Error summarizing"))
                }
            } catch (e: Exception) {
                summarizationState.emit(SummarizationState.Error(e.message?: "Unknown error"))
            }
        }
    }

    companion object {
        fun provideFactory(
            post: Post
        ): ViewModelProvider.Factory = object: ViewModelProvider.Factory {

            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as JetnewsApplication
                return ArticleViewModel(
                    post,
                    application.container.geminiRepository
                ) as T
            }

        }
    }

}