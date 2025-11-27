package com.example.mynotes.data

import com.example.mynotes.domain.ContentItem
import com.example.mynotes.domain.Note
import com.example.mynotes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NotesDao,
    private val imageFileManager: ImageFileManager
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val note = Note(0, title, content.processForStorage(), updatedAt, isPinned)
        val noteDbModel = note.toDbModel()
        noteDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        val imagesUrlsToDel =
            noteDao.getNote(noteId)
                .toEntity().content
                .filterIsInstance<ContentItem.Image>()
                .map { it.url }

        noteDao.deleteNote(noteId)

        imagesUrlsToDel.forEach {
            imageFileManager.DeleteImageFromInternalStorage(it)
        }
    }

    override suspend fun editNote(note: Note) {
        val oldNote = noteDao.getNote(note.id).toEntity()

        val oldUrls = oldNote.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val newUrls = note.content.filterIsInstance<ContentItem.Image>().map { it.url }

        val removedUrls = oldUrls - newUrls

        removedUrls.forEach {
            imageFileManager.DeleteImageFromInternalStorage(it)
        }

        val processedContent = note.content.processForStorage()
        val processedNote = note.copy(content = processedContent)
        noteDao.addNote(processedNote.toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { it.ToEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return noteDao.getNote(noteId).toEntity()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { it.ToEntities() }
    }

    override suspend fun switchPinStatus(noteId: Int) {
        noteDao.switchPinnedStatus(noteId)
    }

    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItem ->
            when (contentItem) {
                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val internalPath =
                            imageFileManager.copyImageToInternalStorage(contentItem.url)
                        ContentItem.Image(internalPath)
                    }
                }

                is ContentItem.Text -> contentItem
            }
        }
    }
}