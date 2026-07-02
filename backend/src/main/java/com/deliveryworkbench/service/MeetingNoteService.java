package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.ai.MeetingNoteSummary;
import com.deliveryworkbench.dto.CreateMeetingNoteRequest;
import com.deliveryworkbench.dto.MeetingNoteDto;
import com.deliveryworkbench.dto.SummarizeMeetingNoteRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.repository.MeetingNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingNoteService {

    private final MeetingNoteRepository meetingNoteRepository;
    private final DeliveryRequestRepository requestRepository;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;

    @Transactional
    public MeetingNoteDto createMeetingNote(Long requestId, CreateMeetingNoteRequest request, String createdBy) {
        DeliveryRequest deliveryRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        MeetingNote meetingNote = MeetingNote.builder()
                .request(deliveryRequest)
                .meetingTitle(request.getMeetingTitle())
                .meetingDate(request.getMeetingDate())
                .attendees(request.getAttendees())
                .discussionSummary(request.getDiscussionSummary())
                .decisions(request.getDecisions())
                .actionItems(request.getActionItems())
                .source(request.getSource())
                .createdBy(createdBy)
                .build();

        meetingNote = meetingNoteRepository.save(meetingNote);
        log.info("Created meeting note {} for request {}", meetingNote.getId(), requestId);
        return mapToDto(meetingNote);
    }

    @Transactional(readOnly = true)
    public List<MeetingNoteDto> getMeetingNotesByRequestId(Long requestId) {
        return meetingNoteRepository.findByRequest_IdOrderByMeetingDateDesc(requestId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MeetingNoteSummary summarizeNotes(Long requestId, SummarizeMeetingNoteRequest request) {
        DeliveryRequest deliveryRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        MeetingNoteSummary summary = aiService.summarizeMeetingNotes(request.getRawNotes());

        aiAuditLogService.logAIAction(
                deliveryRequest,
                AIActionType.SUMMARIZE_MEETING,
                request.getRawNotes(),
                "Summary:\n" + summary.getDiscussionSummary() + "\nDecisions:\n" + summary.getDecisions() + "\nAction Items:\n" + summary.getActionItems()
        );

        return summary;
    }

    private MeetingNoteDto mapToDto(MeetingNote note) {
        return MeetingNoteDto.builder()
                .id(note.getId())
                .requestId(note.getRequest().getId())
                .meetingTitle(note.getMeetingTitle())
                .meetingDate(note.getMeetingDate())
                .attendees(note.getAttendees())
                .discussionSummary(note.getDiscussionSummary())
                .decisions(note.getDecisions())
                .actionItems(note.getActionItems())
                .createdBy(note.getCreatedBy())
                .source(note.getSource())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
