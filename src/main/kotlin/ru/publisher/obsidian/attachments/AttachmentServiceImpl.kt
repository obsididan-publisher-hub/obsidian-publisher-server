package ru.publisher.obsidian.attachments

import org.springframework.stereotype.Service

@Service
class AttachmentServiceImpl(
    private val attachmentResources: Map<String, Attachment>
) : AttachmentService {
    /**
     * @param attachemntId идентификатор заметки
     */
    override fun getAttachment(attachemntId: String): Attachment? =
        attachmentResources.get(attachemntId)

    override fun getAllAttachments(): Set<Attachment> = attachmentResources.values.toSet()
}