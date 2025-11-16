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
        public val NOTE_LINK_REGEX =
            Regex("""(?<!\\)\[\[([^]|#]+)(?:#([^]|]+))?(?:\|([^]]+))?]]""")

        /**
         * Извлекает ссылки на заметки вида [[Link]] из текста заметки, не учитывая экранирование
         *
         * @param content содержимое заметки
         * @return список ссылок на странице
         */
        fun extractLinks(content: String): Set<Link> {
            return NOTE_LINK_REGEX.findAll(content).map { match ->
                val original: String = match.value
                val path: String = match.groupValues[1].trim()
                val extension: String = path.substringAfterLast('.', NOTE_EXTENSION)
                val section = match.groupValues.getOrNull(2)?.takeIf { it.isNotEmpty() }
                val alias = match.groupValues.getOrNull(3)?.takeIf { it.isNotEmpty() }
                Link(original, path, extension, section, alias)
            }.toSet()
        }

        /**
         * Вычисляет идентификатор ресурса obsidian как MD5-хеш в строковом представлении от имени заметки (не включая расширение заметки)
         *
         * @param fullName полное имя заметки [Note.fullName] без расширения
         * @return идентификатор заметки
         */
        fun calculateResourceId(fullName: String): String {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(fullName.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}
