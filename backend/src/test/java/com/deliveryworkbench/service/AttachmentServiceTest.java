package com.deliveryworkbench.service;

import com.deliveryworkbench.dto.RequestAttachmentDto;
import com.deliveryworkbench.entity.AttachmentCategory;
import com.deliveryworkbench.entity.DeliveryRequest;
import com.deliveryworkbench.entity.RequestAttachment;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.RequestAttachmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private RequestAttachmentRepository attachmentRepository;

    @Mock
    private DeliveryRequestRepository requestRepository;

    @InjectMocks
    private AttachmentService service;

    private DeliveryRequest request;
    private final String testUploadDir = "./test-attachments";

    @BeforeEach
    void setUp() throws IOException {
        request = DeliveryRequest.builder().id(1L).build();
        ReflectionTestUtils.setField(service, "uploadDir", testUploadDir);
        Files.createDirectories(Paths.get(testUploadDir));
    }

    @AfterEach
    void tearDown() throws IOException {
        Path path = Paths.get(testUploadDir);
        if (Files.exists(path)) {
            Files.list(path).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Files.delete(path);
        }
    }

    @Test
    void uploadAttachment_ShouldSaveFileAndMetadata() throws IOException {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        RequestAttachment savedAttachment = RequestAttachment.builder()
                .id(100L)
                .request(request)
                .fileName("test.txt")
                .attachmentCategory(AttachmentCategory.REQUIREMENT)
                .build();

        given(attachmentRepository.save(any())).willReturn(savedAttachment);

        RequestAttachmentDto dto = service.uploadAttachment(1L, file, AttachmentCategory.REQUIREMENT, "user1");

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getFileName()).isEqualTo("test.txt");

        // Verify file was written
        long count = Files.list(Paths.get(testUploadDir)).count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void deleteAttachment_ShouldRemoveFileAndMetadata() throws IOException {
        Path tempFile = Files.createTempFile(Paths.get(testUploadDir), "test", ".txt");
        
        RequestAttachment attachment = RequestAttachment.builder()
                .id(100L)
                .request(request)
                .storagePath(tempFile.toString())
                .build();

        given(attachmentRepository.findByIdAndRequest_Id(100L, 1L)).willReturn(Optional.of(attachment));

        service.deleteAttachment(1L, 100L);

        verify(attachmentRepository).delete(attachment);
        assertThat(Files.exists(tempFile)).isFalse();
    }
}
