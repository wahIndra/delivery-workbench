package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.ReleaseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReleaseScheduleRepository extends JpaRepository<ReleaseSchedule, Long> {
    List<ReleaseSchedule> findByRequestIdOrderByPlannedReleaseDateAsc(Long requestId);
}
