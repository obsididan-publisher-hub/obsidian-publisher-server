package ru.publisher.obsidian.core.notes

import kotlin.io.path.Path
import kotlin.io.path.name

/**
 * Расширение файла заметки
 */
const val NOTE_EXTENSION = "md"

/**
 * Сервис, хранящий заметки хранилища в памяти
 *
 * @fenya
 * @since 2025.09.20
 */
class NoteServiceImpl(
    private val notesGraph: Map<String, Note>,
    private val startNoteId: String,
) : NoteService {

    override fun getStartNote(): Note {
        return getNoteById(startNoteId)
    }

    override fun getNoteById(noteId: String): Note =
        notesGraph.getOrElse(noteId) {
            throw NoteNotExistException("Note '$noteId' not found.")
        }

    override fun getAllNotes(): Set<Note> = notesGraph.values.toSet()

    override fun getNoteName(note: Note): String = Path(note.fullName).name.substringBeforeLast('.')
}