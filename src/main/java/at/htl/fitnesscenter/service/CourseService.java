package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.model.Course;
import at.htl.fitnesscenter.repository.CourseRepository;
import at.htl.fitnesscenter.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Course> listAll() { return courseRepository.findAll(); }

    public Optional<Course> findById(Long id) { return courseRepository.findById(id); }

    public Course create(Course course) { return courseRepository.save(course); }

    public boolean hasSpace(Long courseId) {
        return courseRepository.findById(courseId)
                .map(c -> {
                    long count = enrollmentRepository.countByCourseIdAndOnWaitlistFalse(courseId);
                    return count < (c.getCapacity() == null ? 20 : c.getCapacity());
                })
                .orElse(false);
    }
}
