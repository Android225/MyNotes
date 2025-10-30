@file:Suppress("OPT_IN_USAGE")

package com.example.mynotes.presentation.screens.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.NotesRepositoryImpl
import com.example.mynotes.domain.GetAllNotesUseCase
import com.example.mynotes.domain.Note
import com.example.mynotes.domain.NotesRepository
import com.example.mynotes.domain.SearchNotesUseCase
import com.example.mynotes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NotesViewModel(context: Context) : ViewModel(){

    private val repository = NotesRepositoryImpl.getInstance(context)

    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)

    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    init {
        query // Обновляем стейт добавляя запрос поиска
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest { input -> // каждое изменение поиска останавливает и переключает поток если надо
                if (input.isBlank()){
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(input)
                }
            }
            .onEach { notes -> // добавляем ноты в стейт для коорректного отображения закрепленных и остальных
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

    //обработка команд
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

//команды экрана NoteScreen
sealed interface NotesCommands{
    data class InputSearchQuery(val query: String): NotesCommands

    data class SwitchPinnedStatus(val noteId: Int): NotesCommands
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
    )