package com.example.mynotes.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase: RoomDatabase() {
}