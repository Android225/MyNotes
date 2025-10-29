package com.example.mynotes.presentation.screens.editing

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Int,
    context: Context = LocalContext.current.applicationContext,
    viewModel: EditNoteViewModel = viewModel {
        EditNoteViewModel(noteId, context)
    },
    onFinished: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value

    when (currentState) {
        is EditNoteState.Editing -> {

        }

        EditNoteState.Finished -> {

        }

        EditNoteState.Initial -> {

        }
    }
}