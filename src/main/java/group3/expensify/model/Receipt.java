package group3.expensify.model;

import jakarta.persistence.*;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String imagePath;  // Path to the image file

    @Column(columnDefinition = "TEXT")
    private String extractedData;  // Data extracted by OCR

    // Getters and Setters
}

