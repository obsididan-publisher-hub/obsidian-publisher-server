package ru.publisher.obsidian.html

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.publisher.obsidian.core.contents.NoteContentService
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.core.notes.NoteService
import java.nio.file.Path

/**
 * Преобразует каждую заметку, найденную в
 */
@Configuration
class NotesHtmlResourcesConfiguration {

    companion object {
        const val HTML_RESOURCES_BEAN = "notesHtmlResources"
    }

    @Bean(HTML_RESOURCES_BEAN)
    fun notesHtmlResources(noteService: NoteService, noteContentService: NoteContentService): Map<Note, Path> {
        return null;
    }
}
