package at.htl.fitnesscenter;

import at.htl.fitnesscenter.dto.CheckInRequest;
import at.htl.fitnesscenter.dto.MemberRequest;
import at.htl.fitnesscenter.dto.PaymentRequest;
import at.htl.fitnesscenter.dto.TrainingPlanRequest;
import at.htl.fitnesscenter.model.*;
import at.htl.fitnesscenter.repository.EmployeeRepository;
import at.htl.fitnesscenter.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void createMember_success() throws Exception {
        MemberRequest request = validMemberRequest("alex@example.com");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("alex@example.com")));
    }

    @Test
    void createMember_duplicateEmail_conflict() throws Exception {
        Member existing = buildMember("dup@example.com", MemberStatus.ACTIVE);
        memberRepository.save(existing);

        MemberRequest request = validMemberRequest("dup@example.com");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getMemberById_success() throws Exception {
        Member member = memberRepository.save(buildMember("get@example.com", MemberStatus.ACTIVE));

        mockMvc.perform(get("/api/members/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(member.getId().intValue())));
    }

    @Test
    void updateMember_success() throws Exception {
        Member member = memberRepository.save(buildMember("update@example.com", MemberStatus.ACTIVE));
        MemberRequest request = validMemberRequest("update@example.com");
        request.setLastName("Updated");

        mockMvc.perform(put("/api/members/{id}", member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Updated")));
    }

    @Test
    void deleteMember_success() throws Exception {
        Member member = memberRepository.save(buildMember("del@example.com", MemberStatus.ACTIVE));

        mockMvc.perform(delete("/api/members/{id}", member.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void paymentForExistingMember_success() throws Exception {
        Member member = memberRepository.save(buildMember("pay@example.com", MemberStatus.ACTIVE));
        PaymentRequest request = new PaymentRequest();
        request.setMemberId(member.getId());
        request.setAmount(new BigDecimal("19.99"));
        request.setPaymentDate(LocalDate.now());
        request.setMethod(PaymentMethod.CARD);
        request.setStatus(PaymentStatus.PAID);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId", is(member.getId().intValue())));
    }

    @Test
    void paymentForMissingMember_notFound() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setMemberId(9999L);
        request.setAmount(new BigDecimal("19.99"));
        request.setPaymentDate(LocalDate.now());
        request.setMethod(PaymentMethod.CARD);
        request.setStatus(PaymentStatus.PAID);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkInOnlyForActiveMember_badRequest() throws Exception {
        Member member = memberRepository.save(buildMember("paused@example.com", MemberStatus.PAUSED));
        CheckInRequest request = new CheckInRequest();
        request.setMemberId(member.getId());
        request.setDate(LocalDate.now());
        request.setTime(LocalTime.of(9, 0));

        mockMvc.perform(post("/api/check-ins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void employeeDelete_requiresAdminRole() throws Exception {
        Employee employee = employeeRepository.save(buildEmployee("emp1@example.com"));

        mockMvc.perform(delete("/api/employees/{id}", employee.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void employeeDelete_adminRole_success() throws Exception {
        Employee employee = employeeRepository.save(buildEmployee("emp2@example.com"));

        mockMvc.perform(delete("/api/employees/{id}", employee.getId())
                        .header("X-Role", "ADMIN"))
                .andExpect(status().isNoContent());
    }

    @Test
    void assignMemberToTrainingPlan_success() throws Exception {
        Employee trainer = employeeRepository.save(buildEmployee("trainer@example.com"));
        Member member = memberRepository.save(buildMember("assign@example.com", MemberStatus.ACTIVE));

        TrainingPlanRequest request = new TrainingPlanRequest();
        request.setTitle("Strength");
        request.setDescription("Plan");
        request.setTrainerId(trainer.getId());

        String response = mockMvc.perform(post("/api/training-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long planId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(post("/api/training-plans/{id}/members/{memberId}", planId, member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberIds", hasItem(member.getId().intValue())));
    }

    private MemberRequest validMemberRequest(String email) {
        MemberRequest request = new MemberRequest();
        request.setFirstName("Alex");
        request.setLastName("Tester");
        request.setDateOfBirth(LocalDate.of(1995, 5, 20));
        request.setAddress("Main Street 1");
        request.setPhoneNumber("123456789");
        request.setEmail(email);
        request.setContractType(ContractType.BASIC);
        request.setContractStart(LocalDate.of(2024, 1, 1));
        request.setContractEnd(LocalDate.of(2025, 1, 1));
        request.setPaymentStatus(PaymentStatus.PAID);
        request.setStatus(MemberStatus.ACTIVE);
        return request;
    }

    private Member buildMember(String email, MemberStatus status) {
        Member member = new Member();
        member.setFirstName("Test");
        member.setLastName("User");
        member.setDateOfBirth(LocalDate.of(1990, 1, 1));
        member.setAddress("Street 1");
        member.setPhoneNumber("111111");
        member.setEmail(email);
        member.setContractType(ContractType.BASIC);
        member.setContractStart(LocalDate.of(2024, 1, 1));
        member.setContractEnd(LocalDate.of(2025, 1, 1));
        member.setPaymentStatus(PaymentStatus.PAID);
        member.setStatus(status);
        return member;
    }

    private Employee buildEmployee(String email) {
        Employee employee = new Employee();
        employee.setFirstName("Emp");
        employee.setLastName("One");
        employee.setEmail(email);
        employee.setPasswordHash("hash");
        employee.setRole(EmployeeRole.ADMIN);
        employee.setPermissions(new java.util.HashSet<>(Set.of("MANAGE")));
        employee.setActive(true);
        return employee;
    }
}
