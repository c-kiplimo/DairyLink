package com.collicode.api.dairylink.web.rest;


import com.collicode.api.dairylink.service.RegistrationService;
import com.collicode.api.dairylink.service.UserService;
import com.collicode.api.dairylink.web.rest.dto.RestResponse;
import com.collicode.api.dairylink.web.rest.request.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/registration")
@AllArgsConstructor
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
}
