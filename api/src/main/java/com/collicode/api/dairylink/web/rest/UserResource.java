package com.collicode.api.dairylink.web.rest;


import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.service.RegistrationService;
import com.collicode.api.dairylink.service.UserService;
import com.collicode.api.dairylink.web.rest.dto.RestResponse;
import com.collicode.api.dairylink.web.rest.request.FarmerRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(path = "api/users")
public class UserResource {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private  final RegistrationService registrationService;

    public UserResource(ModelMapper modelMapper, UserService userService, RegistrationService registrationService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.registrationService = registrationService;
    }

    @PostMapping
    ResponseEntity<?> addNewFarmer(@RequestBody FarmerRequest farmerRequest, @AuthenticationPrincipal User userDetails){
        log.info("request to add new farmer");
        try{
            User newFarmerUser;
            newFarmerUser=userService.addNewFarmer(userDetails,farmerRequest);
            if (newFarmerUser !=null){
                FarmerRequest response = modelMapper.map(newFarmerUser,FarmerRequest.class);
                return  new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                return  new ResponseEntity<>(new RestResponse(true,"Farmer not created"),HttpStatus.OK);
            }
        }catch (Exception e){
            log.error("Error",e);
            return  new ResponseEntity<>(new RestResponse(true, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }
}
