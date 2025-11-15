package com.example.mynotes.presentation.screens.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.data.NotesRepositoryImpl
import com.example.mynotes.domain.AddNoteUseCase
import com.example.mynotes.domain.ContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase : AddNoteUseCase
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
                        previous.copy(
                            content = command.content,
                            isSaveEnable = previous.content.isNotBlank() && previous.title.isNotBlank()
                        )
                    } else {
                        CreationNoteState.Creation(content = command.content)
                    }
                }
            }

            is CreationNoteCommand.InputTitle -> {
                _state.update { previous ->
                    if (previous is CreationNoteState.Creation) {
                        previous.copy(
                            title = command.title,
                            isSaveEnable = previous.content.isNotBlank() && previous.title.isNotBlank()
                        )
                    } else {
                        CreationNoteState.Creation(title = command.title)
                    }
                }
            }

            CreationNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previous ->
                        if (previous is CreationNoteState.Creation) {
                            val title = previous.title
                            val content = ContentItem.Text(content =  previous.content)
                            //temp listof()
                            addNoteUseCase(title = title, listOf(content))
                            CreationNoteState.Finished
                        } else {
                            previous
                        }
                    }
                }
            }
        }

    }
}

sealed interface CreationNoteCommand {

    data class InputTitle(val title: String) : CreationNoteCommand

    data class InputContent(val content: String) : CreationNoteCommand

    data object Save : CreationNoteCommand

    data object Back : CreationNoteCommand
}

sealed interface CreationNoteState {

    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnable: Boolean = false
    ) : CreationNoteState

    data object Finished : CreationNoteState
}