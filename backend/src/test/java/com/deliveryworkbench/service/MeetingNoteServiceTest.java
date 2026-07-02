package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.ai.MeetingNoteSummary;
import com.deliveryworkbench.dto.CreateMeetingNoteRequest;
import com.deliveryworkbench.dto.MeetingNoteDto;
import com.deliveryworkbench.dto.SummarizeMeetingNoteRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.MeetingNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MeetingNoteServiceTest {

    @Mock
    private MeetingNoteRepository meetingNoteRepository;

    @Mock
    private DeliveryRequestRepository requestRepository;

    @Mock
    private AIService aiService;

    @Mock
    private AIAuditLogService aiAuditLogService;

    @InjectMocks
    private MeetingNoteService service;

    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        request = DeliveryRequest.builder().id(1L).build();
    }

    @Test
    void createMeetingNote_ShouldCreateNote() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));

        CreateMeetingNoteRequest req = new CreateMeetingNoteRequest();
        req.setMeetingTitle("Test Title");
        req.setMeetingDate(LocalDate.now());
        req.setSource(MeetingNoteSource.HUMAN);

        MeetingNote savedNote = MeetingNote.builder()
                .id(100L)
                .request(request)
                .meetingTitle(req.getMeetingTitle())
                .meetingDate(req.getMeetingDate())
                .source(req.getSource())
                .createdBy("user")
                .createdAt(OffsetDateTime.now())
                .build();

        given(meetingNoteRepository.save(any())).willReturn(savedNote);

        MeetingNoteDto dto = service.createMeetingNote(1L, req, "user");

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getMeetingTitle()).isEqualTo("Test Title");
    }

    @Test
    void summarizeNotes_ShouldCallAIAndAudit() {
        given(requestRepository.findById(1L)).willReturn(Optional.of(request));

        MeetingNoteSummary mockSummary = MeetingNoteSummary.builder()
                .discussionSummary("Summary")
                .decisions("Decisions")
                .actionItems("Actions")
                .build();

        given(aiService.summarizeMeetingNotes("raw text")).willReturn(mockSummary);

        SummarizeMeetingNoteRequest req = new SummarizeMeetingNoteRequest();
        req.setRawNotes("raw text");

        MeetingNoteSummary summary = service.summarizeNotes(1L, req);

        assertThat(summary.getDiscussionSummary()).isEqualTo("Summary");
        verify(aiAuditLogService).logAIAction(eq(request), eq(AIActionType.SUMMARIZE_MEETING), eq("raw text"), any());
    }
}
