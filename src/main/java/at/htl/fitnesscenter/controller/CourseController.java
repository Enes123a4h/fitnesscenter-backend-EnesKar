package at.htl.fitnesscenter.controller;

import at.htl.fitnesscenter.model.Course;
import at.htl.fitnesscenter.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService service;
    public CourseController(CourseService service) { this.service = service; }

    @GetMapping
    public List<Course> list() { return service.listAll(); }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course c) {
        Course created = service.create(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public Course get(@PathVariable Long id) {
        return service.findById(id).orElseThrow(() -> new at.htl.fitnesscenter.exception.ResourceNotFoundException("Course not found"));
    }
}
