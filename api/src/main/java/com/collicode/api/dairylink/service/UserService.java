package com.collicode.api.dairylink.service;

import com.collicode.api.dairylink.domain.ConfirmationToken;
import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.repository.UserRepository;
import com.collicode.api.dairylink.service.mapper.CooperativeMapper;
import com.collicode.api.dairylink.service.mapper.FarmerMapper;
import com.collicode.api.dairylink.service.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final static String USER_NOT_FOUND_MSG = "User with Email %s not found!";
    private final static String USER_EXISTS = "Email %s Taken!";
    private final CooperativeMapper cooperativeMapper;
    private  final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserMapper userMapper;
    private final FarmerMapper farmerMapper;

    public UserService(CooperativeMapper cooperativeMapper, UserRepository userRepository, ConfirmationTokenService confirmationTokenService, UserMapper userMapper, FarmerMapper farmerMapper) {
        this.cooperativeMapper = cooperativeMapper;
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.userMapper = userMapper;
        this.farmerMapper = farmerMapper;
    }
    public List<Object> signupFarmerAsUser(User user){
        log.info("Signup up User");
        boolean userEmailExists = userRepository.findByEmail(user.getEmail())
                .isPresent();

        if (userEmailExists) {
            throw new IllegalStateException(String.format(USER_EXISTS, user.getEmail()));
        }
        //set Details
        user.setPassword(user.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("System");

        //save the User in the database
        User user1 =userRepository.save(user);
        log.info("Farmer User saved");

        //Generate a Random 6 digit OTP -0-999999
        int randomOTP = (int) ((Math.random() * (999999 - 1)) + 1);
        String token = String.format("%06d", randomOTP);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60*48), // expires after 2 days of generation
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        log.info("Confirmation token generated");

        List<Object> response = new ArrayList<>();
        response.add(user1.getId());
        response.add(token);

        return response;
    }
}
