package group3.expensify.service;

import group3.expensify.model.Currency;
import group3.expensify.model.User;
import group3.expensify.repository.CurrencyRepository;
import group3.expensify.repository.UserRepository;
import group3.expensify.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    public void registerUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        // Hash the password using SHA-256
        String hashedPassword = PasswordUtil.hashPassword(password);
        user.setPassword(hashedPassword);

        Currency defaultCurrency = currencyRepository.findByCurrencyCode("MYR")
                .orElseThrow(() -> new RuntimeException("Default currency MYR not found"));
        user.setDefaultCurrency(defaultCurrency);
        userRepository.save(user);
    }

    public User loginUser(String email, String plainPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null; // user not found
        }

        String hashedInput = PasswordUtil.hashPassword(plainPassword);
        if (user.getPassword().equals(hashedInput)) {
            return user; // password matched
        }

        return null; // password mismatch
    }

    public void updateUserSettings(Long userId, String name, String email, Long currencyId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setName(name);
        user.setEmail(email);

        if (currencyId != null) {
            user.setDefaultCurrency(currencyRepository.findById(currencyId).orElse(null));
        }

        userRepository.save(user);
    }
}