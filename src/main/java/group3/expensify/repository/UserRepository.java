package group3.expensify.repository;

import group3.expensify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here if needed
    User findByEmail(String email);  // Example of a custom query to find a user by email

    User findByEmailAndPassword(String email, String password);
}
