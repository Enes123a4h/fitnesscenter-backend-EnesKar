package at.htl.fitnesscenter.service;

import at.htl.fitnesscenter.dto.TrainingPlanRequest;
import at.htl.fitnesscenter.dto.TrainingPlanResponse;
import at.htl.fitnesscenter.exception.ResourceNotFoundException;
import at.htl.fitnesscenter.model.Employee;
import at.htl.fitnesscenter.model.Member;
import at.htl.fitnesscenter.model.TrainingPlan;
import at.htl.fitnesscenter.repository.EmployeeRepository;
import at.htl.fitnesscenter.repository.MemberRepository;
import at.htl.fitnesscenter.repository.TrainingPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {

    private static final Logger log = LoggerFactory.getLogger(TrainingPlanService.class);

    private final TrainingPlanRepository trainingPlanRepository;
    private final EmployeeRepository employeeRepository;
    private final MemberRepository memberRepository;

    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository,
                               EmployeeRepository employeeRepository,
                               MemberRepository memberRepository) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.employeeRepository = employeeRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public TrainingPlanResponse create(TrainingPlanRequest request) {
        Employee trainer = employeeRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        TrainingPlan plan = new TrainingPlan();
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setTrainer(trainer);
        TrainingPlan saved = trainingPlanRepository.save(plan);
        log.info("Created training plan {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TrainingPlanResponse> getAll() {
        return trainingPlanRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TrainingPlanResponse getById(Long id) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        return toResponse(plan);
    }

    @Transactional
    public TrainingPlanResponse update(Long id, TrainingPlanRequest request) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        Employee trainer = employeeRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setTrainer(trainer);
        TrainingPlan saved = trainingPlanRepository.save(plan);
        log.info("Updated training plan {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        plan.getMembers().forEach(member -> member.getTrainingPlans().remove(plan));
        plan.getMembers().clear();
        trainingPlanRepository.delete(plan);
        log.info("Deleted training plan {}", id);
    }

    @Transactional
    public TrainingPlanResponse assignMember(Long planId, Long memberId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        plan.getMembers().add(member);
        member.getTrainingPlans().add(plan);
        TrainingPlan saved = trainingPlanRepository.save(plan);
        log.info("Assigned member {} to training plan {}", memberId, planId);
        return toResponse(saved);
    }

    @Transactional
    public TrainingPlanResponse removeMember(Long planId, Long memberId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Training plan not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        plan.getMembers().remove(member);
        member.getTrainingPlans().remove(plan);
        TrainingPlan saved = trainingPlanRepository.save(plan);
        log.info("Removed member {} from training plan {}", memberId, planId);
        return toResponse(saved);
    }

    private TrainingPlanResponse toResponse(TrainingPlan plan) {
        TrainingPlanResponse response = new TrainingPlanResponse();
        response.setId(plan.getId());
        response.setTitle(plan.getTitle());
        response.setDescription(plan.getDescription());
        response.setTrainerId(plan.getTrainer().getId());
        Set<Long> memberIds = plan.getMembers().stream()
                .map(Member::getId)
                .collect(Collectors.toSet());
        response.setMemberIds(memberIds);
        return response;
    }
}
