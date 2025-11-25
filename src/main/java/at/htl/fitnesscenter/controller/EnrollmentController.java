package at.htl.fitnesscenter.controller;

import at.htl.fitnesscenter.model.Enrollment;
import at.htl.fitnesscenter.service.EnrollmentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService service;
    public EnrollmentController(EnrollmentService service) { this.service = service; }

    @PostMapping("/enroll")
    public Enrollment enroll(@RequestParam Long userId, @RequestParam Long courseId) {
        return service.enroll(userId, courseId);
    }
}
