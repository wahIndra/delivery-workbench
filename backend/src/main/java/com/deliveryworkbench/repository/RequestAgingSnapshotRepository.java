package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.RequestAgingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestAgingSnapshotRepository extends JpaRepository<RequestAgingSnapshot, Long> {
    List<RequestAgingSnapshot> findByRequest_IdOrderByCalculatedAtDesc(Long requestId);
}
