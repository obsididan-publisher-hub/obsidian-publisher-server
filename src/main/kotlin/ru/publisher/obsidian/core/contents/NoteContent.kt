package ru.publisher.obsidian.core.contents

import ru.publisher.obsidian.core.contents.frontmatter.Frontmatter

/**
 * Описывает данные в заметки
 *
 * @property frontmatter поля заметки
 * @property body содержимое заметки
 */
data class NoteContent(val frontmatter: Frontmatter, val body: String)
