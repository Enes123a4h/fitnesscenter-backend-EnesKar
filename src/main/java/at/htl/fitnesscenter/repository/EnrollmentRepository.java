package at.htl.fitnesscenter.repository;

import at.htl.fitnesscenter.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByUserId(Long userId);
    long countByCourseIdAndOnWaitlistFalse(Long courseId);
}
