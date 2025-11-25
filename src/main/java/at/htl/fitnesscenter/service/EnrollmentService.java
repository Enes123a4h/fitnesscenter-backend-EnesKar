package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.model.Course;
import at.htl.fitnesscenter.model.Enrollment;
import at.htl.fitnesscenter.model.User;
import at.htl.fitnesscenter.repository.CourseRepository;
import at.htl.fitnesscenter.repository.EnrollmentRepository;
import at.htl.fitnesscenter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Enrollment enroll(Long userId, Long courseId) {
        Optional<User> u = userRepository.findById(userId);
        Optional<Course> c = courseRepository.findById(courseId);
        if (u.isEmpty() || c.isEmpty()) throw new IllegalArgumentException("User or Course not found");

        long confirmed = enrollmentRepository.countByCourseIdAndOnWaitlistFalse(courseId);
        int capacity = c.get().getCapacity() == null ? 20 : c.get().getCapacity();
        boolean waitlist = confirmed >= capacity;

        Enrollment e = new Enrollment(u.get(), c.get(), waitlist);
        return enrollmentRepository.save(e);
    }
}
