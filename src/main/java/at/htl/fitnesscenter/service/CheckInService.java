package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.dto.CheckInRequest;
import at.htl.fitnesscenter.dto.CheckInResponse;
import at.htl.fitnesscenter.exception.BadRequestException;
import at.htl.fitnesscenter.exception.ResourceNotFoundException;
import at.htl.fitnesscenter.model.CheckIn;
import at.htl.fitnesscenter.model.Member;
import at.htl.fitnesscenter.model.MemberStatus;
import at.htl.fitnesscenter.repository.CheckInRepository;
import at.htl.fitnesscenter.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CheckInService {

    private static final Logger log = LoggerFactory.getLogger(CheckInService.class);

    private final CheckInRepository checkInRepository;
    private final MemberRepository memberRepository;

    public CheckInService(CheckInRepository checkInRepository, MemberRepository memberRepository) {
        this.checkInRepository = checkInRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public CheckInResponse create(CheckInRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BadRequestException("Member must be active to check in");
        }
        CheckIn checkIn = new CheckIn();
        checkIn.setMember(member);
        checkIn.setDate(request.getDate());
        checkIn.setTime(request.getTime());
        CheckIn saved = checkInRepository.save(checkIn);
        log.info("Created check-in {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CheckInResponse> getAll() {
        return checkInRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CheckInResponse getById(Long id) {
        CheckIn checkIn = checkInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Check-in not found"));
        return toResponse(checkIn);
    }

    @Transactional
    public CheckInResponse update(Long id, CheckInRequest request) {
        CheckIn checkIn = checkInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Check-in not found"));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BadRequestException("Member must be active to check in");
        }
        checkIn.setMember(member);
        checkIn.setDate(request.getDate());
        checkIn.setTime(request.getTime());
        CheckIn saved = checkInRepository.save(checkIn);
        log.info("Updated check-in {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!checkInRepository.existsById(id)) {
            throw new ResourceNotFoundException("Check-in not found");
        }
        checkInRepository.deleteById(id);
        log.info("Deleted check-in {}", id);
    }

    private CheckInResponse toResponse(CheckIn checkIn) {
        CheckInResponse response = new CheckInResponse();
        response.setId(checkIn.getId());
        response.setMemberId(checkIn.getMember().getId());
        response.setDate(checkIn.getDate());
        response.setTime(checkIn.getTime());
        return response;
    }
}
