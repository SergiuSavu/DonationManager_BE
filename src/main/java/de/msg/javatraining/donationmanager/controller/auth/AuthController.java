package de.msg.javatraining.donationmanager.controller.auth;


import de.msg.javatraining.donationmanager.config.security.JwtUtils;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.RefreshTokenService;
import de.msg.javatraining.donationmanager.service.userDetailsService.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/auth")
public class AuthController {
  private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshTokenCookie";
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private RefreshTokenService refreshTokenService;


  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);

    List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    
    String refreshToken = UUID.randomUUID().toString();
    refreshTokenService.deleteRefreshTokenForUser(userDetails.getId());
    refreshTokenService.createRefreshToken(refreshToken, userDetails.getId());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, createCookie(refreshToken).toString());
    SignInResponse signInResponse = new SignInResponse(jwt, refreshToken, userDetails.getId(),
            userDetails.getUsername(), userDetails.getEmail(), roles);

    return new ResponseEntity<>(signInResponse, headers, HttpStatus.OK);
  }

  @GetMapping("/refreshToken")
  public ResponseEntity<?> checkCookie(HttpServletRequest request){
    Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName()
            .equals(REFRESH_TOKEN_COOKIE_NAME)).findFirst();
    if (cookie.isPresent()){
      return ResponseEntity.ok(new RefreshTokenResponse(
              refreshTokenService.exchangeRefreshToken(cookie.get().getValue())
      ));
    }
    throw new RuntimeException("Cookie was not set");
  }

  private ResponseCookie createCookie(String refreshToken) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .httpOnly(true)
            .maxAge(Duration.ofDays(1))
            .sameSite("None")
            .path("/auth/refreshToken")
            .build();
  }


}
