package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.RequirementVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementVersionRepository extends JpaRepository<RequirementVersion, Long> {
    List<RequirementVersion> findByRequirement_IdOrderByVersionDesc(Long requirementId);
    List<RequirementVersion> findByRequestIdOrderByVersionDesc(Long requestId);
}
