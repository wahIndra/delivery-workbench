package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.AttachmentCategory;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RequestAttachmentDto {
    private Long id;
    private Long requestId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String uploadedBy;
    private AttachmentCategory attachmentCategory;
    private OffsetDateTime createdAt;
}
