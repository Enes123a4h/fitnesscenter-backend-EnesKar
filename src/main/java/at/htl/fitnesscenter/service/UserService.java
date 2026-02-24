package at.htl.fitnesscenter.service;

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
        return repository.save(user);
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public User update(Long id, User updatedUser) {
        User existing = repository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setFirstname(updatedUser.getFirstname());
        existing.setLastname(updatedUser.getLastname());
        existing.setEmail(updatedUser.getEmail());
        existing.setPassword(updatedUser.getPassword());

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
