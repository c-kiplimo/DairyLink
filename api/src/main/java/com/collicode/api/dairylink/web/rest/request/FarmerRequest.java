package com.collicode.api.dairylink.web.rest.request;


import lombok.Data;

@Data
public class FarmerRequest {
    private String firstName;
    private String lastName;
    private String msisdn;
    private String email;
    private String county;
    private String subCounty;
    private String  ward;
    private String  village;
    private String password;
}
