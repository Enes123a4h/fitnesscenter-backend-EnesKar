package at.htl.fitnesscenter.controller;

import at.htl.fitnesscenter.dto.TrainerRequest;
import at.htl.fitnesscenter.dto.TrainerResponse;
import at.htl.fitnesscenter.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    public ResponseEntity<TrainerResponse> create(@Valid @RequestBody TrainerRequest request) {
        TrainerResponse response = trainerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<TrainerResponse> getAll() {
        return trainerService.getAll();
    }

    @GetMapping("/{id}")
    public TrainerResponse getById(@PathVariable Long id) {
        return trainerService.getById(id);
    }

    @PutMapping("/{id}")
    public TrainerResponse update(@PathVariable Long id, @Valid @RequestBody TrainerRequest request) {
        return trainerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trainerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
