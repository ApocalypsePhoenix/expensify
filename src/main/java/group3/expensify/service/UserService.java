package group3.expensify.service;

import group3.expensify.model.Currency;
import group3.expensify.model.User;
import group3.expensify.repository.CurrencyRepository;
import group3.expensify.repository.UserRepository;
import group3.expensify.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    // Use a path relative to the project root
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/avatars/";

    public void registerUser(String name, String email, String password, String currencyCode) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));

        Currency selectedCurrency = currencyRepository.findByCurrencyCode(currencyCode)
                .orElseThrow(() -> new RuntimeException("Currency " + currencyCode + " not found"));
        user.setDefaultCurrency(selectedCurrency);
        userRepository.save(user);
    }

    public User loginUser(String email, String plainPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(PasswordUtil.hashPassword(plainPassword))) {
            return user;
        }
        return null;
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

    public void updateProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Clean filename and generate unique ID
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        Path filePath = uploadPath.resolve(fileName);

        // Use StandardCopyOption to ensure overwrite/correct stream handling
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save relative web path
        user.setProfileImagePath("/uploads/avatars/" + fileName);
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}