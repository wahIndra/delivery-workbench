package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.RiskRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskRegisterRepository extends JpaRepository<RiskRegister, Long> {
    List<RiskRegister> findByRequestIdOrderByCreatedAtDesc(Long requestId);
}
