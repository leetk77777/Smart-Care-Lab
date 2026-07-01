package org.opensource.safety.dto;

import java.util.List;

public record AiAnalyzeResponse(
    int score,
    String status,
    List<String> reasons
) {
}
