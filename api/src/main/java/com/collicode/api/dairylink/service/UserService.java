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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    //check if farmer exists
    public  boolean doesUserExistsByEmailAddress(String email){
        Optional<User> farmer = userRepository.findUserByEmail(email);
        return  farmer.isPresent();
    }
   public User addNewFarmer(User userDetails, FarmerRequest farmerRequest){
        log.info("add a new farmer");

        //check if farmer already exists
       if (doesUserExistsByEmailAddress(farmerRequest.getEmail())){
           throw new IllegalStateException("Farmer with provided email already exists ");
       }
       User user = new User();
       log.info("Creating new farmer started");
       if (userDetails!=null){
           user.setCreatedBy(userDetails.getFullName());
           user.setAddedBy(userDetails);
       }
       user.setFirstName(farmerRequest.getFirstName());
       user.setLastName(farmerRequest.getLastName());
       user.setFullName(farmerRequest.getFirstName() + ' ' +user.getLastName());
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
               LocalDateTime.now().plusMinutes(60*48),
               user
       );
       confirmationTokenService.saveConfirmationToken(confirmationToken);
       log.info("Confirmation token generated");

       return  saveFarmer;



   }
}
