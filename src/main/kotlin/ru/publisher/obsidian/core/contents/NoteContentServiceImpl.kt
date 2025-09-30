package ru.publisher.obsidian.core.contents

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.publisher.obsidian.core.notes.Note
import java.io.File

/**
 * Реализация [NoteContentService] через перечитывание содержимого заметок из связанного хранилища
 *
 * @param vaultPath путь к хранилищу obsidian
 * @param frontMatterOffsets сдвиги от начала контента заметки до его содержимого
 */
@Service
class NoteContentServiceImpl(
    @Value("\${obsidian.vault.path}")
    private val vaultPath: String,
) : NoteContentService {
    override fun getFullContent(note: Note): String {
        return File("$vaultPath/${note.fullName}").readText();
    }
}