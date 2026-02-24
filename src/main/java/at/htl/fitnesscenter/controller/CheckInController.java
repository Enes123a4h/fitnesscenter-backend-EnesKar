package at.htl.fitnesscenter.controller;

import at.htl.fitnesscenter.dto.CheckInRequest;
import at.htl.fitnesscenter.dto.CheckInResponse;
import at.htl.fitnesscenter.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public ResponseEntity<CheckInResponse> create(@Valid @RequestBody CheckInRequest request) {
        CheckInResponse response = checkInService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CheckInResponse> getAll() {
        return checkInService.getAll();
    }

    @GetMapping("/{id}")
    public CheckInResponse getById(@PathVariable Long id) {
        return checkInService.getById(id);
    }

    @PutMapping("/{id}")
    public CheckInResponse update(@PathVariable Long id, @Valid @RequestBody CheckInRequest request) {
        return checkInService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        checkInService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
