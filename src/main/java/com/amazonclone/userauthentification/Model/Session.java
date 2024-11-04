package com.amazonclone.userauthentification.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends Base {
    private String token;

    @ManyToOne
    private User user;
}
