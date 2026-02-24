package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.exception.ConflictException;
import at.htl.fitnesscenter.exception.ResourceNotFoundException;
import at.htl.fitnesscenter.model.User;
import at.htl.fitnesscenter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User create(User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        return repository.save(user);
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User update(Long id, User updatedUser) {
        User existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!existing.getEmail().equalsIgnoreCase(updatedUser.getEmail())
                && repository.existsByEmail(updatedUser.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        existing.setFirstname(updatedUser.getFirstname());
        existing.setLastname(updatedUser.getLastname());
        existing.setEmail(updatedUser.getEmail());
        existing.setPassword(updatedUser.getPassword());

        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        repository.deleteById(id);
    }
}
