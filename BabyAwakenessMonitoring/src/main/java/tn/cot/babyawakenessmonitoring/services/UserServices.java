package tn.cot.babyawakenessmonitoring.services;

import jakarta.ejb.EJBException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.apache.commons.lang3.tuple.Pair;
import tn.cot.babyawakenessmonitoring.entities.User;
import tn.cot.babyawakenessmonitoring.enums.Role;
import tn.cot.babyawakenessmonitoring.repositories.UserRepository;
import tn.cot.babyawakenessmonitoring.utils.Argon2Utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class UserServices {

    @Inject
    UserRepository userRepository;
    @Inject
    Argon2Utils argon2Utils;
    @Inject
    EmailService emailService;

    private final Map<String, Pair<String, LocalDateTime>> activationCodes = new HashMap<>();

    public void registerUser(@Valid User user) throws MessagingException {

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new EJBException("User with username " + user.getEmail() + " already exists");
        }

        user.setCreationDate(LocalDateTime.now().toLocalDate().toString());
        user.setRoles(Collections.singleton(Role.USER));
        user.hashPassword(user.getPassword(), argon2Utils);
        userRepository.save(user);
        String activationCode = GenerateActivationCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5); // Set expiration time
        activationCodes.put(activationCode, Pair.of(user.getEmail(),expirationTime));
        emailService.sendEmail(emailService.smtpUser, user.getEmail(), "Activate Account", activationCode);
    }

    public void activateUser(String code) {
        if (activationCodes.containsKey(code)) {

            Pair<String, LocalDateTime> codeDetails = activationCodes.get(code);
            LocalDateTime expirationTime = codeDetails.getRight();
            if (LocalDateTime.now().isAfter(expirationTime)) {
                activationCodes.remove(code);
                throw new EJBException("Activation code expired");
            }
            String email = codeDetails.getLeft();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user!=null) {
                user.setAccountActivated(true);
                userRepository.save(user);
                activationCodes.remove(code);
            } else {
                throw new IllegalArgumentException("User not found.");
            }
        }
    }


    private String GenerateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public User authenticateUser(String email, String password) {
        final User user = userRepository.findByEmail(email).orElseThrow(() -> new EJBException("User not found"));
        if(user != null && argon2Utils.check(user.getPassword(), password.toCharArray()) && user.getAccountActivated()==true){
            return user;
        }
        throw new EJBException("Failed log in with email: " + email + " [Unknown email or wrong password or account not activated] ");
    }
}







