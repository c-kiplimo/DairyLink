package com.collicode.api.dairylink.service;


import com.collicode.api.dairylink.domain.ConfirmationToken;
import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.domain.enums.CooperativeStatus;
import com.collicode.api.dairylink.domain.enums.UserRole;
import com.collicode.api.dairylink.repository.UserRepository;
import com.collicode.api.dairylink.util.EmailValidator;
import com.collicode.api.dairylink.util.NotificationHelper;
import com.collicode.api.dairylink.web.rest.request.ForgotPassword;
import com.collicode.api.dairylink.web.rest.request.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void register(RegistrationRequest registrationRequest) {
        log.info("Registering  new User");
        boolean isValidemail = emailValidator.test(registrationRequest.getEmail());

        //Exception handling logic
        if (!isValidemail) {
            throw new IllegalStateException(String.format(EMAIL_NOT_VALID, registrationRequest.getEmail()));

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
        User cooperative = new User();
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
        log.info("User saved", savedCooperative);

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

        User user =(User) response.get(0);

        //Sending confirmation OTP
        String token1 =(String) response.get(1);
        NotificationHelper.sendConfirmationToken(token1, "CONFIRM", user);


    }

    @Transactional
    public String confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(() ->
                new IllegalStateException("Token not Found!"));

        if(confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("Email Already Confirmed!");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if(expiredAt.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Token Expired!");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(confirmationToken.getUser().getEmail());
        return "Confirmed! You can now Login to your account";
    }

    public String requestOTP(String msisdn, String resend) {
        log.info("Generating OTP");

        Optional<User> userOptional = userService.findByMsisdn(msisdn);

        if (userOptional.isEmpty()) {
            throw new IllegalStateException(String.format(PHONE_NOT_VALID, msisdn));
        }
        User user = userOptional.get();
        // Generate a Random 6 digit OTP - 0 - 999999
        int randomOTP = (int) ((Math.random() * (999999 - 1)) + 1);
        String token = String.format("%06d", randomOTP);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5), // Expires after 5 minutes
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        log.info("Reset OTP generated");
        //sending confirmation OTP
        NotificationHelper.sendConfirmationToken(token, "RESET", user);
        return "OTP SENT TO " + msisdn;
    }
    // Reset Password
    public String reset(ForgotPassword forgotPassword) {
        log.info("Resetting Password");

        Optional<User> userOptional = userService.findByMsisdn(forgotPassword.getMsisdn());

        if (userOptional.isEmpty()) {
            throw new IllegalStateException(String.format(PHONE_NOT_VALID, forgotPassword.getMsisdn()));
        }
        User user = userOptional.get();

        // VERIFY TOKEN
        confirmToken(forgotPassword.getOtp());
        // Add user
        String encodedPassword = passwordEncoder.encode(forgotPassword.getPassword());

        // Set details
        user.setPassword(encodedPassword);

        // save the User in the database
        userRepository.save(user);
        log.info("User Updated Successfully");

        return "PASSWORD CHANGED SUCCESSFULLY";
    }
}
