package at.htl.fitnesscenter.repository;

import at.htl.fitnesscenter.model.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
}
