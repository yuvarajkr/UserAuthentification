package com.amazonclone.userauthentification.Service;

import com.amazonclone.userauthentification.Model.User;

public interface IAuthService {
    User signUp(String email, String password);
    User login(String email, String password);
    User logout(String Email);
}
