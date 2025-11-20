package com.example.mynotes.presentation.screens.creation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.AddNoteUseCase
import com.example.mynotes.domain.ContentItem
import com.example.mynotes.domain.ContentItem.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CreationNoteState>(CreationNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreationNoteCommand) {
        when (command) {
            CreationNoteCommand.Back -> {
                _state.update { CreationNoteState.Finished }
            }

            is CreationNoteCommand.InputContent -> {
                _state.update { previous ->
                    if (previous is CreationNoteState.Creation) {
                        val newContent = previous.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(content = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        previous.copy(
                            content = newContent
                        )
                    } else {
                        previous
                    }
                }
            }

            is CreationNoteCommand.InputTitle -> {
                _state.update { previous ->
                    if (previous is CreationNoteState.Creation) {
                        previous.copy(
                            title = command.title
                        )
                    } else {
                        previous
                    }
                }
            }


            CreationNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previous ->
                        if (previous is CreationNoteState.Creation) {
                            val title = previous.title
                            val content = previous.content.filter {
                                it !is ContentItem.Text || it.content.isNotBlank()
                            }
                            addNoteUseCase(title = title, content = content)
                            CreationNoteState.Finished
                        } else {
                            previous
                        }
                    }
                }
            }

            is CreationNoteCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is CreationNoteState.Creation) {
                        previousState.content.toMutableList().apply {
                            val lastItem = last()
                            if(lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(Image(url = command.uri.toString()))
                            add(Text(""))
                        }.let {
                            previousState.copy(content = it)
                        }
                    } else {
                        previousState
                    }
                }
            }

            is CreationNoteCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is CreationNoteState.Creation) {
                        previousState.content.toMutableList().apply {
                            removeAt(index = command.index)
                        }.let {
                            previousState.copy(content = it)
                        }
                    } else {
                        previousState
                    }
                }
            }
        }

    }
}

sealed interface CreationNoteCommand {

    data class InputTitle(val title: String) : CreationNoteCommand

    data class InputContent(val content: String, val index: Int) : CreationNoteCommand

    data class AddImage(val uri: Uri) : CreationNoteCommand

    data class DeleteImage(val index: Int): CreationNoteCommand
    data object Save : CreationNoteCommand

    data object Back : CreationNoteCommand
}

sealed interface CreationNoteState {

    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text(""))
    ) : CreationNoteState {

        val isSaveEnable: Boolean
            get() {
                return when {
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : CreationNoteState
}