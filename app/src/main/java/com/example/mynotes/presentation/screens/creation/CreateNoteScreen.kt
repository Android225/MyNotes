package com.example.mynotes.presentation.screens.creation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current.applicationContext,
    viewModel: CreateNoteViewModel = viewModel {
        CreateNoteViewModel(context)
    },
    onFinished: () -> Unit
){}