package at.htl.fitnesscenter.repository;

import at.htl.fitnesscenter.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    boolean existsByMemberId(Long memberId);
}
