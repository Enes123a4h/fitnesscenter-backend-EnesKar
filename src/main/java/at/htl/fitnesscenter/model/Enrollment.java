package at.htl.fitnesscenter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id","course_id"})
})
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "on_waitlist")
    private boolean onWaitlist = false;

    public Enrollment() {}

    public Enrollment(User user, Course course, boolean onWaitlist) {
        this.user = user;
        this.course = course;
        this.onWaitlist = onWaitlist;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public boolean isOnWaitlist() { return onWaitlist; }
    public void setOnWaitlist(boolean onWaitlist) { this.onWaitlist = onWaitlist; }
}
