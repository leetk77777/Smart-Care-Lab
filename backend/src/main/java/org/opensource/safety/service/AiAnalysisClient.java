package org.opensource.safety.service;

import java.util.List;
import org.opensource.safety.dto.AiAnalyzeRequest;
import org.opensource.safety.dto.AiAnalyzeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiAnalysisClient {
    private final RestClient restClient;

    public AiAnalysisClient(@Value("${app.ai-server-url}") String aiServerUrl) {
        this.restClient = RestClient.builder().baseUrl(aiServerUrl).build();
    }

    public AiAnalyzeResponse analyze(AiAnalyzeRequest request) {
        try {
            return restClient.post()
                .uri("/analyze")
                .body(request)
                .retrieve()
                .body(AiAnalyzeResponse.class);
        } catch (RuntimeException ex) {
            return new AiAnalyzeResponse(
                45,
                "CAUTION",
                List.of("AI 분석 서버 연결에 실패하여 보수적으로 주의 상태로 판단했습니다.")
            );
        }
    }
}
