package com.example.mynotes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mynotes.presentation.screens.creation.CreateNoteScreen
import com.example.mynotes.presentation.screens.editing.EditNoteScreen
import com.example.mynotes.presentation.screens.notes.NoteScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            EditNoteScreen(
                noteId = 5,
                onFinished = {}
            )

//            CreateNoteScreen(
//                onFinished = {}
//            )
//            NoteScreen(
//                onNoteClick = {},
//                onAddNoteClick = {}
//            )
        }
    }
}
