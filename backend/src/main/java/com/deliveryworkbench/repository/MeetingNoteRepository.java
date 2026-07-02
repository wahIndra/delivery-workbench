package com.deliveryworkbench.repository;

import com.deliveryworkbench.entity.MeetingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingNoteRepository extends JpaRepository<MeetingNote, Long> {
    List<MeetingNote> findByRequest_IdOrderByMeetingDateDesc(Long requestId);
}
