package com.amazonclone.userauthentification.Dto;

import com.amazonclone.userauthentification.Model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenDto {
    private String token;
    private Long user_id;
}
