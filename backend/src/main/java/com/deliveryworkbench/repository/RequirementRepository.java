package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    List<Requirement> findByRequest_IdOrderByVersionDesc(Long requestId);
    Optional<Requirement> findTopByRequest_IdOrderByVersionDesc(Long requestId);
    boolean existsByRequest_Id(Long requestId);
}
