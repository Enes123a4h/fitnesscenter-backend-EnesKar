package at.htl.fitnesscenter.controller;

import at.htl.fitnesscenter.model.Course;
import at.htl.fitnesscenter.service.CourseService;
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
    public Course create(@RequestBody Course c) { return service.create(c); }

    @GetMapping("/{id}")
    public Course get(@PathVariable Long id) {
        return service.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }
}
