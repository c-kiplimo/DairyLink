package com.collicode.api.dairylink.domain;

import com.collicode.api.dairylink.domain.enums.CooperativeStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_cooperatives")
public class Cooperative {
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Id
    private Long id;
    private String cooperativeName;
    private String email;
    private String msisdn;


    @Enumerated(EnumType.STRING)
    private CooperativeStatus cooperativeStatus;

    //Management details
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String createdBy;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;
    private String lastUpdatedBy;

    //Relations
    @ManyToOne
    private User user;


}
