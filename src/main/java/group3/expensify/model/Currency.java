package group3.expensify.model;

import jakarta.persistence.*;

@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String currencyCode;

    @Column(nullable = false)
    private String currencySymbol;

    // Getters and Setters
}

