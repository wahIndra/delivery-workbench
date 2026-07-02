package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.AnswerClarificationQuestionRequest;
import com.deliveryworkbench.dto.ClarificationQuestionResponse;
import com.deliveryworkbench.dto.CreateClarificationQuestionRequest;
import com.deliveryworkbench.dto.UpdateClarificationQuestionRequest;
import com.deliveryworkbench.service.ClarificationQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/clarifications")
@RequiredArgsConstructor
public class ClarificationQuestionController {

    private final ClarificationQuestionService questionService;

    @GetMapping
    public ResponseEntity<List<ClarificationQuestionResponse>> getQuestions(@PathVariable Long requestId) {
        return ResponseEntity.ok(questionService.getQuestionsByRequestId(requestId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'ADMIN')")
    public ResponseEntity<ClarificationQuestionResponse> createQuestion(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateClarificationQuestionRequest request) {
        ClarificationQuestionResponse response = questionService.createQuestion(requestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/ai-generate")
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'ADMIN')")
    public ResponseEntity<ClarificationQuestionResponse> generateAiQuestions(@PathVariable Long requestId) {
        ClarificationQuestionResponse response = questionService.generateAiQuestions(requestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ANALYST', 'ADMIN')")
    public ResponseEntity<ClarificationQuestionResponse> updateQuestion(
            @PathVariable Long requestId,
            @PathVariable Long questionId,
            @Valid @RequestBody UpdateClarificationQuestionRequest request) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, request));
    }

    @PostMapping("/{questionId}/answer")
    @PreAuthorize("hasAnyRole('BUSINESS_USER', 'ADMIN')")
    public ResponseEntity<ClarificationQuestionResponse> answerQuestion(
            @PathVariable Long requestId,
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerClarificationQuestionRequest request) {
        return ResponseEntity.ok(questionService.answerQuestion(questionId, request));
    }
}
