package com.collicode.api.dairylink.service;

import com.collicode.api.dairylink.domain.ConfirmationToken;
import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.domain.enums.FarmerStatus;
import com.collicode.api.dairylink.domain.enums.UserRole;
import com.collicode.api.dairylink.repository.UserRepository;
import com.collicode.api.dairylink.service.mapper.CooperativeMapper;
import com.collicode.api.dairylink.service.mapper.FarmerMapper;
import com.collicode.api.dairylink.service.mapper.UserMapper;
import com.collicode.api.dairylink.web.rest.request.FarmerRequest;
import com.collicode.api.dairylink.web.rest.request.UserTokenConfirmRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserService  implements UserDetailsService {
    private final static String USER_NOT_FOUND_MSG = "User with Email %s not found!";
    private final static String USER_EXISTS = "Email %s Taken!";
    private final CooperativeMapper cooperativeMapper;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserMapper userMapper;
    private final FarmerMapper farmerMapper;

    private final   PasswordEncoder passwordEncoder;

    public UserService(CooperativeMapper cooperativeMapper, UserRepository userRepository, ConfirmationTokenService confirmationTokenService, UserMapper userMapper, FarmerMapper farmerMapper, PasswordEncoder passwordEncoder) {
        this.cooperativeMapper = cooperativeMapper;
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.userMapper = userMapper;
        this.farmerMapper = farmerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    //check if farmer exists
    public boolean doesUserExistsByEmailAddress(String email) {
        Optional<User> farmer = userRepository.findUserByEmail(email);
        return farmer.isPresent();
    }

    public User addNewFarmer(User userDetails, FarmerRequest farmerRequest) {
        log.info("add a new farmer");

        //check if farmer already exists
        if (doesUserExistsByEmailAddress(farmerRequest.getEmail())) {
            throw new IllegalStateException("Farmer with provided email already exists ");
        }
        User user = new User();
        log.info("Creating new farmer started");
        if (userDetails != null) {
            user.setCreatedBy(userDetails.getFullName());
            user.setAddedBy(userDetails);
        }
        user.setFirstName(farmerRequest.getFirstName());
        user.setLastName(farmerRequest.getLastName());
        user.setFullName(farmerRequest.getFirstName() + ' ' + user.getLastName());
        user.setMsisdn(farmerRequest.getMsisdn());
        user.setEmail(farmerRequest.getEmail());
        user.setUsername(farmerRequest.getEmail());
        user.setFarmerStatus(FarmerStatus.ACTIVE);
        user.setUserRole(UserRole.FARMER);
        user.setCounty(farmerRequest.getCounty());
        user.setSubcounty(farmerRequest.getSubCounty());
        user.setWard(farmerRequest.getWard());
        user.setWard(farmerRequest.getWard());
        user.setCreatedAt(LocalDateTime.now());

        //save farmer
        User saveFarmer = userRepository.save(user);

        //Generate a Random 6 digit OTP - 0-999999
        int randomOTP = (int) ((Math.random() * (999999 - 1)) + 1);
        String token = String.format("%06d", randomOTP);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60 * 48),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        log.info("Confirmation token generated");

        return saveFarmer;


    }

    public User enableAppUser(String email) {
        //Request UserDto rather than all details
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));

        user.setEnabled(true);
        userRepository.save(user);
        return user;
    }

    public Optional<User> confirmFarmerTokenAndUpdatePassword(UserTokenConfirmRequest userTokenConfirmRequest) {
        log.info("Request to confirm Farmer token and update password: {}", userTokenConfirmRequest);
        Optional<User> userOptional = userRepository.findById(userTokenConfirmRequest.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            ConfirmationToken confirmationToken = confirmationTokenService.getToken(userTokenConfirmRequest.getToken()).orElseThrow(() ->
                    new IllegalStateException("Token not Found!"));

            if (confirmationToken != null) {
                if (confirmationToken.getConfirmedAt() != null) {
                    throw new IllegalStateException("Email already confirmed");
                } else {
                    LocalDateTime expiredAt = confirmationToken.getExpiresAt();
                    if (expiredAt.isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("Token has expired");
                    } else {
                        String encodedPassword = passwordEncoder.encode(userTokenConfirmRequest.getPassword());
                        user.setPassword(encodedPassword);
                        user.setFarmerStatus(FarmerStatus.ACTIVE);
                        userRepository.save(user);
                        confirmationTokenService.setConfirmedAt(confirmationToken.getToken());
                        enableAppUser(confirmationToken.getUser().getEmail());
                        log.info("User password updated successfully");
                        return userOptional;
                    }
                }
            } else {
                throw new IllegalStateException("Invalid Token");
            }
        } else {
            throw new IllegalStateException("Invalid Farmer");
        }
    }
    public Optional<User> findByMsisdn(String msisdn) {
        log.info("Request to find user with phone : {}", msisdn);

        return userRepository.findByMsisdn(msisdn);
    }
    public Optional<User> findByEmail(String email) {
        log.info("Request to find user with email : {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        log.info("Found user : {}", user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }
}