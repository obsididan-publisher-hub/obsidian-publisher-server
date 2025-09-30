package ru.publisher.obsidian.core.contents

import ru.publisher.obsidian.core.notes.Note

/**
 * Сервис, работающий с содержимым заметок
 */
interface NoteContentService {

    /**
     * Содержимое заметки (как содержимое файла), включая frontmatter
     */
    fun getFullContent(note: Note): String
}