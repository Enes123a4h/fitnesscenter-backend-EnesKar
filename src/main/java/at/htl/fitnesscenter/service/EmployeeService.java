package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.dto.EmployeeRequest;
import at.htl.fitnesscenter.dto.EmployeeResponse;
import at.htl.fitnesscenter.exception.ConflictException;
import at.htl.fitnesscenter.exception.ForbiddenException;
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
import java.util.Set;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        Employee employee = new Employee();
        applyRequest(employee, request);
        Employee saved = employeeRepository.save(employee);
        log.info("Created employee {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return toResponse(employee);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && employeeRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        applyRequest(employee, request);
        Employee saved = employeeRepository.save(employee);
        log.info("Updated employee {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id, String requesterRole) {
        if (requesterRole == null || !EmployeeRole.ADMIN.name().equalsIgnoreCase(requesterRole)) {
            throw new ForbiddenException("Only admins can delete employees");
        }
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found");
        }
        employeeRepository.deleteById(id);
        log.info("Deleted employee {}", id);
    }

    private void applyRequest(Employee employee, EmployeeRequest request) {
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        employee.setRole(request.getRole());
        Set<String> permissions = request.getPermissions();
        if (permissions != null) {
            employee.setPermissions(permissions);
        }
        employee.setActive(request.isActive());
    }

    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setRole(employee.getRole());
        response.setPermissions(employee.getPermissions());
        response.setActive(employee.isActive());
        return response;
    }
}
