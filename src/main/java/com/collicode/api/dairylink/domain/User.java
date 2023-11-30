package com.collicode.api.dairylink.domain;


import com.collicode.api.dairylink.domain.enums.CooperativeStatus;
import com.collicode.api.dairylink.domain.enums.FarmerStatus;
import com.collicode.api.dairylink.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "tbl_users")

public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private  String fullName;
    private String firstName;
    private String lastName;
    private String county;
    private String subcounty;
    private String ward;

    @ManyToOne
    @JoinColumn(name = "addedBy")
    User addedBy;
    @Column(unique = true)
    private String msisdn;
    @Column(unique = true)
    private String email;

    //LOGIN DETAILS
    private String username;
    private String password;

    @Enumerated(EnumType.STRING) //COOPERATIVE,FARMER
    private UserRole userRole;


    //FARMER DETAILS
    private String village;
    @Enumerated(EnumType.STRING)
    private FarmerStatus farmerStatus;
    private int numberOfCows;
    private int numberOfLactatingCows;

    private  String createdBy;
    private String lastUpdatedBy;


    //management fields
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;

    //Cooperative Details
    private String cooperativeName;
    @Enumerated(EnumType.STRING)
    private CooperativeStatus cooperativeStatus;
    // Access fields
    private Boolean locked = false;
    private Boolean enabled = false;

    public User(){

    }
    // User Registration Constructor
    public User(String firstName, String lastName, String email, String msisdn, String password, UserRole userRole) {
        this.fullName = firstName + ' ' + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = email;
        this.msisdn = msisdn;
        this.password = password;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRole.name());

        return Collections.singletonList(grantedAuthority);

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", cooperativeName='" + cooperativeName + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}