package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.RequestAttachmentDto;
import com.deliveryworkbench.entity.AttachmentCategory;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.RequestAttachment;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequestAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final RequestAttachmentRepository attachmentRepository;
    private final DeliveryRequestRepository requestRepository;

    @Value("${attachment.upload-dir:./attachments}")
    private String uploadDir;

    @Transactional
    public RequestAttachmentDto uploadAttachment(Long requestId, MultipartFile file, AttachmentCategory category, String uploadedBy) {
        DeliveryRequest deliveryRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file.");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            RequestAttachment attachment = RequestAttachment.builder()
                    .request(deliveryRequest)
                    .fileName(originalFileName)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .storagePath(targetLocation.toString())
                    .uploadedBy(uploadedBy)
                    .attachmentCategory(category)
                    .build();

            attachment = attachmentRepository.save(attachment);
            log.info("Uploaded file {} for request {}", attachment.getId(), requestId);
            
            return mapToDto(attachment);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<RequestAttachmentDto> getAttachmentsByRequestId(Long requestId) {
        return attachmentRepository.findByRequest_IdOrderByCreatedAtDesc(requestId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Resource loadFileAsResource(Long requestId, Long attachmentId) {
        RequestAttachment attachment = attachmentRepository.findByIdAndRequest_Id(attachmentId, requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        try {
            Path filePath = Paths.get(attachment.getStoragePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + attachment.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + attachment.getFileName());
        }
    }
    
    @Transactional(readOnly = true)
    public RequestAttachmentDto getAttachmentMetadata(Long requestId, Long attachmentId) {
        RequestAttachment attachment = attachmentRepository.findByIdAndRequest_Id(attachmentId, requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));
        return mapToDto(attachment);
    }

    @Transactional
    public void deleteAttachment(Long requestId, Long attachmentId) {
        RequestAttachment attachment = attachmentRepository.findByIdAndRequest_Id(attachmentId, requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        try {
            Path filePath = Paths.get(attachment.getStoragePath()).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("Could not delete file {} from filesystem", attachment.getStoragePath(), ex);
        }

        attachmentRepository.delete(attachment);
        log.info("Deleted attachment {}", attachmentId);
    }

    private RequestAttachmentDto mapToDto(RequestAttachment attachment) {
        return RequestAttachmentDto.builder()
                .id(attachment.getId())
                .requestId(attachment.getRequest().getId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .uploadedBy(attachment.getUploadedBy())
                .attachmentCategory(attachment.getAttachmentCategory())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
