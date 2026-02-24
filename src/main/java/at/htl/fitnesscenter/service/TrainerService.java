package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.dto.TrainerRequest;
import at.htl.fitnesscenter.dto.TrainerResponse;
import at.htl.fitnesscenter.exception.ResourceNotFoundException;
import at.htl.fitnesscenter.model.Employee;
import at.htl.fitnesscenter.model.EmployeeRole;
import at.htl.fitnesscenter.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TrainerResponse create(TrainerRequest request) {
        Employee employee = new Employee();
        String name = request.getName().trim();
        String[] parts = name.split("\\s+");
        employee.setFirstName(parts[0]);
        employee.setLastName(parts.length > 1 ? parts[parts.length - 1] : "Trainer");
        employee.setRole(EmployeeRole.TRAINER);
        employee.setExpertise(request.getExpertise());
        employee.setActive(true);

        String email = generateEmail(name);
        employee.setEmail(email);
        employee.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));

        Employee saved = employeeRepository.save(employee);
        log.info("Created trainer {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> getAll() {
        return employeeRepository.findByRole(EmployeeRole.TRAINER).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TrainerResponse getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .filter(e -> e.getRole() == EmployeeRole.TRAINER)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        return toResponse(employee);
    }

    @Transactional
    public TrainerResponse update(Long id, TrainerRequest request) {
        Employee employee = employeeRepository.findById(id)
                .filter(e -> e.getRole() == EmployeeRole.TRAINER)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        String name = request.getName().trim();
        String[] parts = name.split("\\s+");
        employee.setFirstName(parts[0]);
        employee.setLastName(parts.length > 1 ? parts[parts.length - 1] : "Trainer");
        employee.setExpertise(request.getExpertise());

        Employee saved = employeeRepository.save(employee);
        log.info("Updated trainer {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .filter(e -> e.getRole() == EmployeeRole.TRAINER)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        employeeRepository.delete(employee);
        log.info("Deleted trainer {}", id);
    }

    private String generateEmail(String name) {
        String base = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", ".");
        if (base.isBlank()) {
            base = "trainer";
        }
        return base + "." + UUID.randomUUID().toString().substring(0, 8) + "@trainer.local";
    }

    private TrainerResponse toResponse(Employee employee) {
        TrainerResponse response = new TrainerResponse();
        response.setId(employee.getId());
        response.setName(employee.getFirstName() + " " + employee.getLastName());
        response.setExpertise(employee.getExpertise());
        return response;
    }
}
