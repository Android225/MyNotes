@file:Suppress("OPT_IN_USAGE")

package com.example.mynotes.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.TestNotesRepositoryImpl
import com.example.mynotes.domain.GetAllNotesUseCase
import com.example.mynotes.domain.GetNoteUseCase
import com.example.mynotes.domain.Note
import com.example.mynotes.domain.SearchNotesUseCase
import com.example.mynotes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NotesViewModel : ViewModel(){

    private val repository = TestNotesRepositoryImpl
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)

    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()



    init {
        query
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest { input ->
                if (input.isBlank()){
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(input)
                }
            }
            .onEach { notes ->
                val pinnedNotes = notes.filter { it.isPinned }
                val otherNotes = notes.filter { !it.isPinned }
                _state.update {
                    it.copy(
                        pinnedNotes = pinnedNotes,
                        otherNotes = otherNotes
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: NotesCommands){
        viewModelScope.launch {
            when(command){
                is NotesCommands.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }
                is NotesCommands.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }
    }
}

sealed interface NotesCommands{
    data class InputSearchQuery(val query: String): NotesCommands

    data class SwitchPinnedStatus(val noteId: Int): NotesCommands
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
    )