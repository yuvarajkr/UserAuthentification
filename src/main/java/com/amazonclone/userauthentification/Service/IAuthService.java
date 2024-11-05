package com.amazonclone.userauthentification.Service;

import com.amazonclone.userauthentification.Model.User;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface IAuthService {
    User signUp(String email, String password);
    Pair<User, MultiValueMap<String,String>> login(String email, String password);
    User logout(String Email);

    Boolean validateToken(String token, Long userId);
}
