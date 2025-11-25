package at.htl.fitnesscenter.repository;

import at.htl.fitnesscenter.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
