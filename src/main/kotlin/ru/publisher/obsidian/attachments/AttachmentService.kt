package ru.publisher.obsidian.attachments

/**
 * Сервис для получения вложений, связанных с заметками
 */
interface AttachmentService {
    /**
     * @param attachemntId идентификатор заметки
     */
    fun getAttachment(attachemntId: String): Attachment?

    fun getAllAttachments(): Set<Attachment>
}