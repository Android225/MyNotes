package com.example.mynotes.data

import com.example.mynotes.domain.ContentItem
import com.example.mynotes.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {
    val contentAsString = Json.encodeToString(content.toContentItemDbModel())
    return NoteDbModel(id, title, contentAsString, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModel(): List<ContentItemDbModel>{
    return map{contentItem ->
        when(contentItem){
            is ContentItem.Image -> {
                ContentItemDbModel.Image(url = contentItem.url)
            }
            is ContentItem.Text -> {
                ContentItemDbModel.Text(context = contentItem.content)
            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItem(): List<ContentItem>{
    return map{contentItem ->
        when(contentItem){
            is ContentItemDbModel.Image -> {
                ContentItem.Image(url = contentItem.url)
            }
            is ContentItemDbModel.Text -> {
                ContentItem.Text(content = contentItem.context)
            }
        }
    }
}

fun NoteDbModel.toEntity(): Note {
    val contentItemDbModel = Json.decodeFromString<List<ContentItemDbModel>>(content)
    return Note(id, title, contentItemDbModel.toContentItem(), updatedAt, isPinned)
}

fun List<NoteDbModel>.ToEntities(): List<Note>{
    return this.map { it.toEntity() }
}
