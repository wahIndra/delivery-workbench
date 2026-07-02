package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.RequestAttachmentDto;
import com.deliveryworkbench.entity.AttachmentCategory;
import com.deliveryworkbench.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "Endpoints for Attachment management")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a new attachment")
    public ResponseEntity<RequestAttachmentDto> uploadAttachment(
            @PathVariable Long requestId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") AttachmentCategory category,
            @RequestHeader(value = "X-User", defaultValue = "system") String user) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(requestId, file, category, user));
    }

    @GetMapping
    @Operation(summary = "Get all attachments for a request")
    public ResponseEntity<List<RequestAttachmentDto>> getAttachments(@PathVariable Long requestId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByRequestId(requestId));
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "Download an attachment")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long requestId,
            @PathVariable Long attachmentId) {
        
        Resource resource = attachmentService.loadFileAsResource(requestId, attachmentId);
        RequestAttachmentDto metadata = attachmentService.getAttachmentMetadata(requestId, attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getFileType() != null ? metadata.getFileType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Delete an attachment")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long requestId,
            @PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(requestId, attachmentId);
        return ResponseEntity.noContent().build();
    }
}
