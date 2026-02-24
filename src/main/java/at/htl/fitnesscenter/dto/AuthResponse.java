package at.htl.fitnesscenter.dto;

public class AuthResponse {

    private String email;

    public AuthResponse() {
    }

    public AuthResponse(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
