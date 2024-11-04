package com.amazonclone.userauthentification.Dto;

import com.amazonclone.userauthentification.Model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {
    private String email;
    private Status status;
}
