package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.MeetingNoteSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMeetingNoteRequest {

    @NotBlank(message = "Meeting title is required")
    private String meetingTitle;

    @NotNull(message = "Meeting date is required")
    private LocalDate meetingDate;

    private String attendees;
    private String discussionSummary;
    private String decisions;
    private String actionItems;

    @NotNull(message = "Source is required")
    private MeetingNoteSource source;
}
