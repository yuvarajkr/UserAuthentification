package com.amazonclone.userauthentification.Controller;

import com.amazonclone.userauthentification.Dto.LoginDto;
import com.amazonclone.userauthentification.Dto.LogoutRequestDto;
import com.amazonclone.userauthentification.Dto.SignUpDto;
import com.amazonclone.userauthentification.Dto.UserDto;
import com.amazonclone.userauthentification.Model.User;
import com.amazonclone.userauthentification.Service.IAuthService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpDto signUpDto){
        if(signUpDto.getEmail()==null || signUpDto.getPassword() == null){
            throw new IllegalArgumentException("Both Email and password are mandatory for signup");
        }

        User user = authService.signUp(signUpDto.getEmail(), signUpDto.getPassword());

        return new ResponseEntity<>(getUserDto(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto){
        if(loginDto.getEmail()==null || loginDto.getPassword() == null){
            throw new IllegalArgumentException("Both Email and password are mandatory for login");
        }

        Pair<User, MultiValueMap<String, String>> userWithHeader = authService.login(loginDto.getEmail(), loginDto.getPassword());

        if(userWithHeader.a==null){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(getUserDto(userWithHeader.a),userWithHeader.b, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<UserDto> logout(@RequestBody LogoutRequestDto logoutRequestDto){
        return null;
    }

    private UserDto getUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
