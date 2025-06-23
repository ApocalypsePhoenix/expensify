package group3.expensify.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // The user associated with the report

    @Column(nullable = false)
    private String timePeriod;  // daily, weekly, monthly

    @Column(columnDefinition = "TEXT")
    private String data;  // Generated report data, potentially in JSON or plain text format

    // Getter and Setter for User
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Getter and Setter for TimePeriod
    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    // Getter and Setter for Data (Report content)
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Custom setter for userId if needed (this is not necessary unless you want to manually set the userId)
    public void setUserId(Long userId) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setId(userId);  // Manually set the userId for user association
    }

    // Custom setter for report content (optional, depending on how you generate the report data)
    public void setContent(String content) {
        this.data = content;  // Set the report content
    }

}
