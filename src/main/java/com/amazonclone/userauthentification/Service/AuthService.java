package com.amazonclone.userauthentification.Service;

import com.amazonclone.userauthentification.Exception.UserAlreadyExistException;
import com.amazonclone.userauthentification.Model.Status;
import com.amazonclone.userauthentification.Model.User;
import com.amazonclone.userauthentification.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User signUp(String email, String password) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);

        if(userOptional.isPresent()){
            throw new UserAlreadyExistException("User with email id already exist, Please login");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        return user;
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("Invalid email address");
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }

    @Override
    public User logout(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        User user = userOptional.get();
        user.setStatus(Status.INACTIVE);
        return user;
    }
}
