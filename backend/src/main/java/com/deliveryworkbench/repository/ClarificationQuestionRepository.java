package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.ClarificationQuestion;
import com.deliveryworkbench.entity.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClarificationQuestionRepository extends JpaRepository<ClarificationQuestion, Long> {
    List<ClarificationQuestion> findByRequest_IdOrderByCreatedAtAsc(Long requestId);
    List<ClarificationQuestion> findByRequest_IdAndStatus(Long requestId, QuestionStatus status);
    long countByRequest_IdAndStatus(Long requestId, QuestionStatus status);
}
