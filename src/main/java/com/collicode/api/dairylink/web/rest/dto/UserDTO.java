package com.collicode.api.dairylink.web.rest.dto;


import com.collicode.api.dairylink.domain.User;
import com.collicode.api.dairylink.domain.enums.CooperativeStatus;
import com.collicode.api.dairylink.domain.enums.FarmerStatus;
import com.collicode.api.dairylink.domain.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String fullName;;
    private String firstName;
    private String lastName;
    private String cooperatveName;
    private String msisdn;
    private String email;
    private String county;
    private String subcounty;
    private String ward;
    private String village;

    //
    User addedBy;

    //LOGIN DETAILS
    private  String username;
    private String password;
    private UserRole userRole;

    private CooperativeStatus cooperativeStatus;
    private FarmerStatus farmerStatus;

    private String createdBy; //full name
    private String lastUpdatedBy; //full name


    // management fields

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;


}
