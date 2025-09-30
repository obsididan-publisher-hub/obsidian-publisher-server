package ru.publisher.obsidian.core.notes

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация [NoteService]. По переданному в пути в *obsidian.vault.path* строит граф
 * заметок и на его основе инициализирует сервис
 */
@Configuration
class NoteServiceConfiguration(
    @Value("\${obsidian.vault.path}")
    private val vaultPath: String
) {
    private val logger: Logger = LoggerFactory.getLogger(NoteServiceConfiguration::class.java)

    /**
     * @return готовый к работе сервис
     */
    @Bean
    fun noteService(): NoteService {
        return NoteServiceImpl(GraphBuilder(vaultPath).build())
    }
}
