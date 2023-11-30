package com.collicode.api.dairylink.web.rest.request;

import com.collicode.api.dairylink.domain.enums.CooperativeStatus;
import com.collicode.api.dairylink.domain.enums.UserRole;
import lombok.Data;

@Data
public class RegistrationRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String cooperativeName;
    private String msisdn;
    private String email;
    private String county;
    private String subcounty;
    private String ward;
    private final String password;
    private UserRole userRole;
    private CooperativeStatus cooperativeStatus;

}
