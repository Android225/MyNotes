package com.example.mynotes.presentation.screens.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.data.TestNotesRepositoryImpl
import com.example.mynotes.domain.AddNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel(context: Context) : ViewModel(){

    private val repository = TestNotesRepositoryImpl
    private val addNoteUseCase = AddNoteUseCase(repository)

    private val _state = MutableStateFlow<CreationNoteState>(CreationNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreationNoteCommand){
        when(command){
            CreationNoteCommand.Back -> {
                _state.update { CreationNoteState.Finished }
            }
            is CreationNoteCommand.InputContent -> {
                _state.update {previous ->
                    if (previous is CreationNoteState.Creation){
                        previous.copy(
                            content = previous.content,
                            isSaveEnable = previous.content.isBlank() && previous.title.isBlank()
                        )
                    } else {
                        CreationNoteState.Creation(content = command.content)
                    }
                }
            }
            is CreationNoteCommand.InputTitle -> {
                _state.update {previous ->
                    if (previous is CreationNoteState.Creation){
                        previous.copy(
                            title = previous.title,
                            isSaveEnable = previous.content.isBlank() && previous.title.isBlank()
                        )
                    } else {
                        CreationNoteState.Creation(title = command.title)
                    }
                }
            }
            CreationNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previous ->
                        if (previous is CreationNoteState.Creation){
                            val title = previous.title
                            val content = previous.content
                            addNoteUseCase(title = title, content = content)
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

sealed interface CreationNoteCommand{

    data class InputTitle(val title: String): CreationNoteCommand

    data class InputContent(val content: String): CreationNoteCommand

    data object Save: CreationNoteCommand

    data object Back: CreationNoteCommand
}

sealed interface CreationNoteState{

    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnable: Boolean = false
    ) : CreationNoteState

    data object Finished: CreationNoteState
}