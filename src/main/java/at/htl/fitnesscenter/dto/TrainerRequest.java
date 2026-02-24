package at.htl.fitnesscenter.dto;

import jakarta.validation.constraints.NotBlank;

public class TrainerRequest {

    @NotBlank
    private String name;

    private String expertise;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }
}
