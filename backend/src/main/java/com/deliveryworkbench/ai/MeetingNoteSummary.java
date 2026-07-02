package com.deliveryworkbench.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingNoteSummary {
    private String discussionSummary;
    private String decisions;
    private String actionItems;
}
