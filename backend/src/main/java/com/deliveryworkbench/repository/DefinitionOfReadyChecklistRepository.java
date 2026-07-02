package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.DefinitionOfReadyChecklist;
import com.deliveryworkbench.entity.ReadyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DefinitionOfReadyChecklistRepository extends JpaRepository<DefinitionOfReadyChecklist, Long> {
    Optional<DefinitionOfReadyChecklist> findByRequest_Id(Long requestId);
    boolean existsByRequest_Id(Long requestId);
    boolean existsByRequest_IdAndReadyStatus(Long requestId, ReadyStatus readyStatus);
}
