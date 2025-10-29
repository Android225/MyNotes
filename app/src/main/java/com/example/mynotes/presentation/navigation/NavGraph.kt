package com.example.mynotes.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavArgument
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mynotes.presentation.screens.creation.CreateNoteScreen
import com.example.mynotes.presentation.screens.editing.EditNoteScreen
import com.example.mynotes.presentation.screens.notes.NoteScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route
    ){
        composable(Screen.Notes.route){
            NoteScreen(
                onNoteClick = {
                    navController.navigate(Screen.EditNote.createRoute(it.id))
                },
                onAddNoteClick = {
                    navController.navigate(Screen.CreateNote.route)
                }
            )
        }
        composable(Screen.CreateNote.route){
            CreateNoteScreen(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditNote.route){
           val noteId = Screen.EditNote.getNoteId(it.arguments)
            EditNoteScreen(
                noteId = noteId,
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String){
    data object Notes: Screen("notes")
    data object CreateNote: Screen("create_note")
    data object EditNote: Screen("edit_note/{note_id}"){

        fun getNoteId(arguments: Bundle?): Int{
            return arguments?.getString("note_id")?.toInt() ?: 0
        }

        fun createRoute(noteId: Int): String{
            return "edit_note/$noteId"
        }
    }
}