package com.tanda.daraja_api.entity;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "auth_token")
@Data
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String token;

    @CreationTimestamp
    Date createdDate;

    @UpdateTimestamp
    Date updatedAt;
}
