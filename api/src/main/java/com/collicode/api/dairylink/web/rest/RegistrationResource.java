package com.collicode.api.dairylink.web.rest;


import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.service.RegistrationService;
import com.collicode.api.dairylink.service.UserService;
import com.collicode.api.dairylink.web.rest.dto.RestResponse;
import com.collicode.api.dairylink.web.rest.request.RegistrationRequest;
import com.collicode.api.dairylink.web.rest.request.UserTokenConfirmRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/registration")
@AllArgsConstructor
@Slf4j
public class RegistrationResource {
    private final RegistrationService registrationService;
    private  final UserService userService;
    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest){
        try{
            registrationService.register(registrationRequest);
            return  new ResponseEntity<>(new RestResponse(false,"User registered successfully!"),
                    HttpStatus.OK);
        }catch (Exception e){
            return  new ResponseEntity<>(new RestResponse(true, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    @GetMapping(path = "confirm")
    public  ResponseEntity<?> confirm(@RequestParam("token") String token){
        try{
            String response = registrationService.confirmToken(token);
            return  new ResponseEntity<>(new RestResponse(false,response),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new RestResponse(true,e.getMessage()),
                   HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    @PostMapping(path = "/confirmFarmerToken")
    ResponseEntity<?> updateAndConfirmFarmerToken(@RequestBody UserTokenConfirmRequest userTokenConfirmRequest){
        log.info("Request to confirm farmer token and password");
        try{
            Optional<User> updateFarmer =userService.confirmFarmerTokenAndUpdatePassword(userTokenConfirmRequest);

            return  new ResponseEntity<>(new RestResponse(false,"CONFIRMED AND PASSWORD UPDATED"),HttpStatus.OK);
        }catch (Exception e){
            return  new ResponseEntity<>(new RestResponse(true, e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
