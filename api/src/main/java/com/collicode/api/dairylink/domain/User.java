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
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

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

    private  String createdBy;
    private String lastUpdatedBy;


    //management fields
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;

    //Object Relationships
    @ManyToOne
    @JoinColumn(name="cooperative_id")
    Cooperative cooperative;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
