package com.amazonclone.userauthentification.Service;

import com.amazonclone.userauthentification.Model.Session;
import com.amazonclone.userauthentification.Repository.SessionRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.http.Cookie;
import org.antlr.v4.runtime.misc.Pair;
import com.amazonclone.userauthentification.Exception.UserAlreadyExistException;
import com.amazonclone.userauthentification.Model.Status;
import com.amazonclone.userauthentification.Model.User;
import com.amazonclone.userauthentification.Repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecretKey secretkey;

    @Autowired
    private SessionRepo sessionRepo;

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
    public Pair<User,MultiValueMap<String,String>> login(String email, String password) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("Invalid email address");
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("Invalid email or password");
        }

        // jwt token -> header + payload + signature(secretKey)

        // payload
        Map<String,Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles());
        long currentTime = System.currentTimeMillis();
        claims.put("iat", currentTime);
        claims.put("exp",currentTime+86400000);
        claims.put("issuer", "Yuvaraj K R");

        //jwt token creation
        String token = Jwts.builder().claims(claims).signWith(secretkey).compact();

        //add token into a cookie and send it in header. As we need to send user as well, we need to create multivalue map
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, token);

        //add the jwt session to session table
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setStatus(Status.ACTIVE);
        sessionRepo.save(session);

        return new Pair<>(user,headers);
    }

    @Override
    public User logout(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        User user = userOptional.get();
        user.setStatus(Status.INACTIVE);
        return user;
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepo.findByTokenAndUser_Id(token,userId);

        if(sessionOptional.isEmpty()){
            System.out.println("Ask user to login");
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretkey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Long exp = (Long)claims.get("exp");
        Long iat = (Long)claims.get("iat");

        if(iat>exp){
            System.out.println("Token expired");
            return false;
        }

        return true;
    }
}
