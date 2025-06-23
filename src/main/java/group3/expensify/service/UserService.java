package group3.expensify.service;

import group3.expensify.model.User;
import group3.expensify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Register a new user (without hashing the password)
    public User register(User user) {
        // No password encoding here, saving password as is
        return userRepository.save(user);  // Save the user to the database
    }

    // Login a user (this is not used for session management, just for decorative purposes)
    public User login(User user) {
        // This would normally authenticate the user, but here we are just saving users without login logic
        return userRepository.findByEmail(user.getEmail());  // Return user if found
    }
}
