package at.htl.fitnesscenter.repository;

import at.htl.fitnesscenter.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByMemberId(Long memberId);
}
