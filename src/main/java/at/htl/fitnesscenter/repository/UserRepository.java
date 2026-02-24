package at.htl.fitnesscenter.repository;

import at.htl.fitnesscenter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
