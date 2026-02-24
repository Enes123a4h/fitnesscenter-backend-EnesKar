package at.htl.fitnesscenter.controller;

import at.htl.fitnesscenter.dto.TrainingPlanRequest;
import at.htl.fitnesscenter.dto.TrainingPlanResponse;
import at.htl.fitnesscenter.service.TrainingPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-plans")
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(TrainingPlanService trainingPlanService) {
        this.trainingPlanService = trainingPlanService;
    }

    @PostMapping
    public ResponseEntity<TrainingPlanResponse> create(@Valid @RequestBody TrainingPlanRequest request) {
        TrainingPlanResponse response = trainingPlanService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<TrainingPlanResponse> getAll() {
        return trainingPlanService.getAll();
    }

    @GetMapping("/{id}")
    public TrainingPlanResponse getById(@PathVariable Long id) {
        return trainingPlanService.getById(id);
    }

    @PutMapping("/{id}")
    public TrainingPlanResponse update(@PathVariable Long id, @Valid @RequestBody TrainingPlanRequest request) {
        return trainingPlanService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trainingPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members/{memberId}")
    public TrainingPlanResponse assignMember(@PathVariable Long id, @PathVariable Long memberId) {
        return trainingPlanService.assignMember(id, memberId);
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public TrainingPlanResponse removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        return trainingPlanService.removeMember(id, memberId);
    }
}
