package ru.publisher.obsidian.core.notes

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.publisher.obsidian.html.IgnoredDirectories

/**
 * Конфигурация [NoteService]. По переданному в пути в *obsidian.vault.path* строит граф
 * заметок и на его основе инициализирует сервис
 */
@Configuration
class NoteServiceConfiguration {
    /**
     * @return готовый к работе сервис
     */
    @Bean
    fun noteService(
        @Value("\${notes.startNote}") startNoteName: String,
        @Value("\${obsidian.vault.path}") vaultPath: String,
        ignoredDirectories: IgnoredDirectories
    ): NoteService =
        NoteServiceImpl(
            NotesCollector(vaultPath, ignoredDirectories.directories).collect(),
            NoteUtils.calculateResourceId(startNoteName)
        )
}
