package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.dto.PaymentRequest;
import at.htl.fitnesscenter.dto.PaymentResponse;
import at.htl.fitnesscenter.exception.ResourceNotFoundException;
import at.htl.fitnesscenter.model.Member;
import at.htl.fitnesscenter.model.Payment;
import at.htl.fitnesscenter.repository.MemberRepository;
import at.htl.fitnesscenter.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    public PaymentService(PaymentRepository paymentRepository, MemberRepository memberRepository) {
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        Payment payment = new Payment();
        payment.setMember(member);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setMethod(request.getMethod());
        payment.setStatus(request.getStatus());
        Payment saved = paymentRepository.save(payment);
        log.info("Created payment {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse update(Long id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        payment.setMember(member);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setMethod(request.getMethod());
        payment.setStatus(request.getStatus());
        Payment saved = paymentRepository.save(payment);
        log.info("Updated payment {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found");
        }
        paymentRepository.deleteById(id);
        log.info("Deleted payment {}", id);
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setMemberId(payment.getMember().getId());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        return response;
    }
}
