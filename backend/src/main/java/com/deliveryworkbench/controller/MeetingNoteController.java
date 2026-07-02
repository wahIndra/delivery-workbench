package com.deliveryworkbench.controller;

import com.deliveryworkbench.ai.MeetingNoteSummary;
import com.deliveryworkbench.dto.CreateMeetingNoteRequest;
import com.deliveryworkbench.dto.MeetingNoteDto;
import com.deliveryworkbench.dto.SummarizeMeetingNoteRequest;
import com.deliveryworkbench.service.MeetingNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/meeting-notes")
@RequiredArgsConstructor
@Tag(name = "Meeting Notes", description = "Endpoints for Meeting Notes management")
public class MeetingNoteController {

    private final MeetingNoteService meetingNoteService;

    @PostMapping
    @Operation(summary = "Create a new meeting note")
    public ResponseEntity<MeetingNoteDto> createMeetingNote(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateMeetingNoteRequest request,
            @RequestHeader(value = "X-User", defaultValue = "system") String user) {
        return ResponseEntity.ok(meetingNoteService.createMeetingNote(requestId, request, user));
    }

    @GetMapping
    @Operation(summary = "Get meeting notes for a request")
    public ResponseEntity<List<MeetingNoteDto>> getMeetingNotes(@PathVariable Long requestId) {
        return ResponseEntity.ok(meetingNoteService.getMeetingNotesByRequestId(requestId));
    }

    @PostMapping("/summarize")
    @Operation(summary = "Summarize raw meeting notes using AI")
    public ResponseEntity<MeetingNoteSummary> summarizeNotes(
            @PathVariable Long requestId,
            @Valid @RequestBody SummarizeMeetingNoteRequest request) {
        return ResponseEntity.ok(meetingNoteService.summarizeNotes(requestId, request));
    }
}
