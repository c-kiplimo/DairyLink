package com.collicode.api.dairylink.service;


import com.collicode.api.dairylink.domain.ConfirmationToken;
import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.domain.enums.CooperativeStatus;
import com.collicode.api.dairylink.domain.enums.UserRole;
import com.collicode.api.dairylink.repository.UserRepository;
import com.collicode.api.dairylink.util.EmailValidator;
import com.collicode.api.dairylink.web.rest.request.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RegistrationService {
    private final static String USER_EXISTS = "Email %s is already taken!";
    private final static String USER_NOT_FOUND_MSG = "user %s not found!";
    private final static String EMAIL_NOT_VALID = "EMAIL %s IS NOT VALID";
    private final static String PHONE_NOT_VALID = "PHONE %s IS NOT VALID";
    private static final String EMAIL_ALREADY_EXISTS = "Email %s already exists";
    private static final String PHONE_EXISTS = "Phone %s already exists";
    private final UserService userService;
    private final EmailValidator emailValidator;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;

    public RegistrationService(@Lazy UserService userService, EmailValidator emailValidator, PasswordEncoder passwordEncoder, UserRepository userRepository, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.emailValidator = emailValidator;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
    }
    public void  register(RegistrationRequest registrationRequest){
        log.info("Registering  new User");
        boolean isValidemail = emailValidator.test(registrationRequest.getEmail());

        //Exception handling logic
        if(!isValidemail){
            throw new IllegalStateException(String.format(EMAIL_NOT_VALID,registrationRequest.getEmail()));

        }
        //check if email and phone number exists
        Optional<User> userOptional = userRepository.findByEmail(registrationRequest.getEmail());
        Optional<User> userOptional1 = userRepository.findByMsisdn(registrationRequest.getMsisdn());
        if (userOptional.isPresent()) {
            throw new IllegalStateException(String.format(USER_EXISTS, registrationRequest.getEmail()));
        }
        if (userOptional1.isPresent()) {
            throw new IllegalStateException(String.format(PHONE_EXISTS, registrationRequest.getMsisdn()));
        }
        //create user
        User cooperative =new User();
        cooperative.setCooperativeName(registrationRequest.getCooperativeName());
        cooperative.setEmail(registrationRequest.getEmail());
        cooperative.setMsisdn(cooperative.getMsisdn());
        cooperative.setUsername(registrationRequest.getEmail());
        cooperative.setUserRole(UserRole.COOPERATIVE);
        cooperative.setCounty(cooperative.getCounty());
        cooperative.setSubcounty(cooperative.getSubcounty());
        cooperative.setWard(cooperative.getWard());
        cooperative.setCooperativeStatus(CooperativeStatus.NEW);
        cooperative.setCreatedBy("SELF-REGISTRATION");


        //encode password
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        cooperative.setPassword(encodedPassword);
        cooperative.setCreatedAt(LocalDateTime.now());

        //save the User in the database
        User savedCooperative = userRepository.save(cooperative);
        log.info("User saved",savedCooperative);

        // Generate a Random 6 digit OTP - 0 - 999999
        int randomOTP = (int) ((Math.random() * (999999 - 1)) + 1);
        String token = String.format("%06d", randomOTP);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), // expires after 15 minutes of generation
                cooperative
        );

confirmationTokenService.saveConfirmationToken(confirmationToken);
log.info("Confirmation token generated");
        List<Object> response = new ArrayList<>();
        response.add(savedCooperative);
        response.add(token);

    }
}
