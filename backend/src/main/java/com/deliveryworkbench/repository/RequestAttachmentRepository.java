package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.RequestAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestAttachmentRepository extends JpaRepository<RequestAttachment, Long> {
    List<RequestAttachment> findByRequest_IdOrderByCreatedAtDesc(Long requestId);
    Optional<RequestAttachment> findByIdAndRequest_Id(Long id, Long requestId);
}
