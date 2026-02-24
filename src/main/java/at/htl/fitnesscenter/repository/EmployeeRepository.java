package at.htl.fitnesscenter.repository;

import at.htl.fitnesscenter.model.Employee;
import at.htl.fitnesscenter.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Employee> findByRole(EmployeeRole role);
}
