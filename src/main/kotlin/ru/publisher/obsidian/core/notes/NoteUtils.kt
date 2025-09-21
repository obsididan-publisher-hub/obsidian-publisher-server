package ru.publisher.obsidian.core.notes

import java.security.MessageDigest


/**
 * Вспомогательные утилитарные методы для работы с содержимым заметок
 *
 * @author fenya
 * @since 2025.09.21
 */
class NoteUtils {

    companion object {
        /**
         * Регулярное выражение для обнаружения ссылок в заметке obsidian
         */
        private val NOTE_LINK_REGEX =
            Regex("""(?<!\\)\[\[([^]|#]+)(?:#([^]|]+))?(?:\|([^]]+))?]]""")

        /**
         * Извлекает ссылки на заметки вида [[Link]] из текста заметки, не учитывая экранирование
         *
         * @param content содержимое заметки
         * @return список ссылок на странице
         */
        fun extractLinks(content: String): Set<NoteLink> {
            return NOTE_LINK_REGEX.findAll(content).map { match ->
                val original = match.value
                val path = match.groupValues[1].trim()
                val section = match.groupValues.getOrNull(2)?.takeIf { it.isNotEmpty() }
                val alias = match.groupValues.getOrNull(3)?.takeIf { it.isNotEmpty() }

                NoteLink(original, path, section, alias)
            }.toSet()
        }

        /**
         * Вычисляет идентификатор заметки как MD5-хеш в строковом представлении от имени заметки.
         *
         * @param fullName полное имя заметки [Note.fullName]
         * @return идентификатор заметки
         */
        fun calculateNoteId(fullName: String): String {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(fullName.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}
