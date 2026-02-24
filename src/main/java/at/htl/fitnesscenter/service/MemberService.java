package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.dto.MemberRequest;
import at.htl.fitnesscenter.dto.MemberResponse;
import at.htl.fitnesscenter.exception.BadRequestException;
import at.htl.fitnesscenter.exception.ConflictException;
import at.htl.fitnesscenter.exception.ResourceNotFoundException;
import at.htl.fitnesscenter.model.Member;
import at.htl.fitnesscenter.model.TrainingPlan;
import at.htl.fitnesscenter.repository.CheckInRepository;
import at.htl.fitnesscenter.repository.MemberRepository;
import at.htl.fitnesscenter.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final CheckInRepository checkInRepository;

    public MemberService(MemberRepository memberRepository,
                         PaymentRepository paymentRepository,
                         CheckInRepository checkInRepository) {
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.checkInRepository = checkInRepository;
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        validateContractDates(request);

        Member member = new Member();
        applyRequest(member, request);
        Member saved = memberRepository.save(member);
        log.info("Created member {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAll() {
        return memberRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        return toResponse(member);
    }

    @Transactional
    public MemberResponse update(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!member.getEmail().equalsIgnoreCase(request.getEmail())
                && memberRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        validateContractDates(request);
        applyRequest(member, request);
        Member saved = memberRepository.save(member);
        log.info("Updated member {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        if (paymentRepository.existsByMemberId(id)) {
            throw new BadRequestException("Member has payments and cannot be deleted");
        }
        if (checkInRepository.existsByMemberId(id)) {
            throw new BadRequestException("Member has check-ins and cannot be deleted");
        }
        member.getTrainingPlans().forEach(plan -> plan.getMembers().remove(member));
        member.getTrainingPlans().clear();
        memberRepository.delete(member);
        log.info("Deleted member {}", id);
    }

    private void validateContractDates(MemberRequest request) {
        if (request.getContractEnd().isBefore(request.getContractStart())) {
            throw new BadRequestException("Contract end cannot be before contract start");
        }
    }

    private void applyRequest(Member member, MemberRequest request) {
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setAddress(request.getAddress());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setEmail(request.getEmail());
        member.setContractType(request.getContractType());
        member.setContractStart(request.getContractStart());
        member.setContractEnd(request.getContractEnd());
        member.setPaymentStatus(request.getPaymentStatus());
        member.setStatus(request.getStatus());
    }

    private MemberResponse toResponse(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setFirstName(member.getFirstName());
        response.setLastName(member.getLastName());
        response.setDateOfBirth(member.getDateOfBirth());
        response.setAddress(member.getAddress());
        response.setPhoneNumber(member.getPhoneNumber());
        response.setEmail(member.getEmail());
        response.setContractType(member.getContractType());
        response.setContractStart(member.getContractStart());
        response.setContractEnd(member.getContractEnd());
        response.setPaymentStatus(member.getPaymentStatus());
        response.setStatus(member.getStatus());
        Set<Long> trainingPlanIds = member.getTrainingPlans().stream()
                .map(TrainingPlan::getId)
                .collect(Collectors.toSet());
        response.setTrainingPlanIds(trainingPlanIds);
        return response;
    }
}
