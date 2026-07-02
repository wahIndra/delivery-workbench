package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.MeetingNoteSource;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class MeetingNoteDto {
    private Long id;
    private Long requestId;
    private String meetingTitle;
    private LocalDate meetingDate;
    private String attendees;
    private String discussionSummary;
    private String decisions;
    private String actionItems;
    private String createdBy;
    private MeetingNoteSource source;
    private OffsetDateTime createdAt;
}
