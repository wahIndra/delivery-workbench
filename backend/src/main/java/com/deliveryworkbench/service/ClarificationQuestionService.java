package com.deliveryworkbench.service;

import com.deliveryworkbench.ai.AIService;
import com.deliveryworkbench.dto.AnswerClarificationQuestionRequest;
import com.deliveryworkbench.dto.ClarificationQuestionResponse;
import com.deliveryworkbench.dto.CreateClarificationQuestionRequest;
import com.deliveryworkbench.dto.UpdateClarificationQuestionRequest;
import com.deliveryworkbench.entity.*;
import com.deliveryworkbench.exception.BusinessRuleViolationException;
import com.deliveryworkbench.exception.ResourceNotFoundException;
import com.deliveryworkbench.mapper.ClarificationQuestionMapper;
import com.deliveryworkbench.repository.ClarificationQuestionRepository;
import com.deliveryworkbench.repository.DeliveryRequestRepository;
import com.deliveryworkbench.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClarificationQuestionService {

    private final ClarificationQuestionRepository questionRepository;
    private final DeliveryRequestRepository requestRepository;
    private final ClarificationQuestionMapper questionMapper;
    private final AIService aiService;
    private final AIAuditLogService aiAuditLogService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ClarificationQuestionResponse> getQuestionsByRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId);
        }
        return questionRepository.findByRequest_IdOrderByCreatedAtAsc(requestId)
                .stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClarificationQuestionResponse createQuestion(Long requestId, CreateClarificationQuestionRequest dto) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        String askedBy = SecurityUtils.getCurrentUsername();
        if (askedBy == null) {
            throw new BusinessRuleViolationException("Must be authenticated to create a question");
        }

        ClarificationQuestion question = ClarificationQuestion.builder()
                .request(request)
                .question(dto.getQuestion())
                .askedBy(askedBy)
                .source(QuestionSource.HUMAN)
                .status(QuestionStatus.OPEN)
                .build();

        question = questionRepository.save(question);
        
        // Notify owner
        if (request.getItOwner() != null) {
            notificationService.createNotification(
                    request.getItOwner(),
                    request,
                    com.deliveryworkbench.entity.NotificationType.CLARIFICATION_REQUIRED,
                    "Clarification Required",
                    "A new clarification question has been asked on request " + request.getRequestCode()
            );
        }
        
        return questionMapper.toResponse(question);
    }

    @Transactional
    public ClarificationQuestionResponse generateAiQuestions(Long requestId) {
        DeliveryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with id: " + requestId));

        // Generate AI draft
        String aiOutput = aiService.generateClarificationQuestions(
                request.getId(),
                request.getTitle(),
                request.getBusinessProblem(),
                request.getProposedChange()
        );

        // BR-03: Save output to AIAuditLog before returning
        String inputPrompt = String.format("Generate clarification questions for: %s | Business Problem: %s | Proposed Change: %s",
                request.getTitle(), request.getBusinessProblem(), request.getProposedChange());
        
        aiAuditLogService.logAIAction(request, AIActionType.GENERATE_CLARIFICATION_QUESTIONS, inputPrompt, aiOutput);

        // BR-04: Save as a draft (OPEN) AI question
        ClarificationQuestion question = ClarificationQuestion.builder()
                .request(request)
                .question(aiOutput)
                .askedBy("AI")
                .source(QuestionSource.AI)
                .status(QuestionStatus.OPEN)
                .build();

        question = questionRepository.save(question);
        return questionMapper.toResponse(question);
    }

    @Transactional
    public ClarificationQuestionResponse updateQuestion(Long questionId, UpdateClarificationQuestionRequest dto) {
        ClarificationQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Clarification question not found with id: " + questionId));

        if (question.getStatus() != QuestionStatus.OPEN) {
            throw new BusinessRuleViolationException("Can only update OPEN questions");
        }

        // BR-04: Human must review and edit before sending. Here we just update the text.
        question.setQuestion(dto.getQuestion());
        
        question = questionRepository.save(question);
        return questionMapper.toResponse(question);
    }

    @Transactional
    public ClarificationQuestionResponse answerQuestion(Long questionId, AnswerClarificationQuestionRequest dto) {
        ClarificationQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Clarification question not found with id: " + questionId));

        if (question.getStatus() != QuestionStatus.OPEN) {
            throw new BusinessRuleViolationException("Can only answer OPEN questions");
        }

        String answeredBy = SecurityUtils.getCurrentUsername();
        if (answeredBy == null) {
            throw new BusinessRuleViolationException("Must be authenticated to answer a question");
        }

        question.setAnswer(dto.getAnswer());
        question.setAnsweredBy(answeredBy);
        question.setAnsweredAt(OffsetDateTime.now());
        question.setStatus(QuestionStatus.ANSWERED);

        question = questionRepository.save(question);
        return questionMapper.toResponse(question);
    }
}
