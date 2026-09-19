package com.easyvyaapaar.controller;

import com.easyvyaapaar.dto.ApiResponse;
import com.easyvyaapaar.dto.AssistantQueryRequest;
import com.easyvyaapaar.service.AssistantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<Map<String, String>>> query(
            @Valid @RequestBody AssistantQueryRequest request) {
        String answer = assistantService.answer(request.getQuery(), request.getLanguage());
        return ResponseEntity.ok(ApiResponse.success(Map.of("answer", answer)));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("EasyVyaapaar Assistant is running."));
    }
}
