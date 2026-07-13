package org.opensource.safety.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import org.opensource.safety.dto.AiAnalyzeRequest;
import org.opensource.safety.dto.AiAnalyzeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AiAnalysisClient {
    private final String aiServerUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AiAnalysisClient(@Value("${app.ai-server-url}") String aiServerUrl) {
        this.aiServerUrl = aiServerUrl;
    }

    public AiAnalyzeResponse analyze(AiAnalyzeRequest request) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(aiServerUrl + "/analyze"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(request)))
                .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("AI server returned " + response.statusCode() + ": " + response.body());
            }
            return parseResponse(response.body());
        } catch (RuntimeException ex) {
            return new AiAnalyzeResponse(
                45,
                "CAUTION",
                List.of("AI 분석 서버 연결에 실패하여 보수적으로 주의 상태로 판단했습니다.")
            );
        } catch (Exception ex) {
            return new AiAnalyzeResponse(
                45,
                "CAUTION",
                List.of("AI 분석 서버 연결에 실패하여 보수적으로 주의 상태로 판단했습니다.")
            );
        }
    }

    private String toJson(AiAnalyzeRequest request) {
        return """
            {
              "seniorId": %d,
              "motionDetected": %s,
              "doorOpened": %s,
              "temperature": %s,
              "humidity": %s,
              "illuminance": %s,
              "eventTime": "%s",
              "lastMotionAt": %s,
              "baselineActiveStartHour": %d,
              "baselineActiveEndHour": %d
            }
            """.formatted(
            request.seniorId(),
            request.motionDetected(),
            request.doorOpened(),
            String.format(Locale.US, "%.2f", request.temperature()),
            String.format(Locale.US, "%.2f", request.humidity()),
            String.format(Locale.US, "%.2f", request.illuminance()),
            request.eventTime(),
            request.lastMotionAt() == null ? "null" : "\"" + request.lastMotionAt() + "\"",
            request.baselineActiveStartHour(),
            request.baselineActiveEndHour()
        );
    }

    private AiAnalyzeResponse parseResponse(String body) {
        int score = Integer.parseInt(extractNumber(body, "score"));
        String status = extractString(body, "status");
        List<String> reasons = extractReasons(body);
        return new AiAnalyzeResponse(score, status, reasons.isEmpty() ? List.of("AI 분석이 완료되었습니다.") : reasons);
    }

    private String extractNumber(String body, String key) {
        String marker = "\"" + key + "\":";
        int start = body.indexOf(marker) + marker.length();
        int end = start;
        while (end < body.length() && Character.isDigit(body.charAt(end))) {
            end++;
        }
        return body.substring(start, end);
    }

    private String extractString(String body, String key) {
        String marker = "\"" + key + "\":\"";
        int start = body.indexOf(marker) + marker.length();
        int end = body.indexOf("\"", start);
        return body.substring(start, end);
    }

    private List<String> extractReasons(String body) {
        List<String> reasons = new ArrayList<>();
        String marker = "\"reasons\":[";
        int start = body.indexOf(marker);
        if (start < 0) {
            return reasons;
        }
        start += marker.length();
        int end = body.indexOf("]", start);
        if (end < 0) {
            return reasons;
        }
        String content = body.substring(start, end);
        for (String part : content.split("\",\"")) {
            String cleaned = part.replace("\"", "").trim();
            if (!cleaned.isBlank()) {
                reasons.add(cleaned);
            }
        }
        return reasons;
    }
}
