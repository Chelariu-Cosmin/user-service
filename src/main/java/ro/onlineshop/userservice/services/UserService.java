package ro.onlineshop.userservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ro.onlineshop.api.beans.UserDto;
import ro.onlineshop.api.payload.request.LoginRequest;
import ro.onlineshop.api.payload.request.TokenRefreshRequest;
import ro.onlineshop.api.payload.response.LoginResponse;
import ro.onlineshop.api.payload.response.TokenRefreshResponse;
import ro.onlineshop.userservice.controllers.event.RegistrationEvent;
import ro.onlineshop.userservice.entities.RefreshToken;
import ro.onlineshop.userservice.entities.User;
import ro.onlineshop.userservice.repositories.RefreshTokenRepository;
import ro.onlineshop.userservice.repositories.UserRepository;
import ro.onlineshop.userservice.services.authentification.RefreshTokenService;
import ro.onlineshop.userservice.services.authentification.UserDetailsImpl;
import ro.onlineshop.userservice.utils.exception.TokenRefreshException;
import ro.onlineshop.userservice.utils.jwt.JwtUtils;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static ro.onlineshop.userservice.mappers.IUserMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final ApplicationEventPublisher publisher;


    @Value("${onlineshop.app.jwtExpirationMs}")
    private Double jwtExpirationMs;

    public LoginResponse authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);
        Date expirationDate = jwtUtils.extractExpirationDate(jwt);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new LoginResponse(jwt, refreshToken.getToken(), expirationDate, userDetails.getId(), userDetails.getEmail(), roles);
    }

    public User registerUser(UserDto signUpRequest, final HttpServletRequest request) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already taken!");
        }

        User user = userRepository.save(INSTANCE.toUser(signUpRequest));
        publisher.publishEvent(new RegistrationEvent(user, applicationUrl(request)));
        return user;
    }

    public RefreshToken generateTokenSendingEmail(User user) {

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        var verificationToken = new RefreshToken(user, jwt);
        refreshTokenRepository.save(verificationToken);
        return new RefreshToken(user, jwt);
    }

    public String verifyEmail(@RequestParam("token") String token) {
        Optional<RefreshToken> theToken = refreshTokenService.findByToken(token);
        if (theToken.get().getUser().isEnabled()) {
            return "This account has already been verified, please, login.";
        }
        String verificationResult = validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")) {
            return "Email verified successfully. Now you can login to your account";
        }
        return null;
    }

    public String validateToken(String theToken) {
        return refreshTokenService.findByToken(theToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    user.setEnabled(true);
                    userRepository.save(user);
                    return "valid";
                })
                .orElse("Invalid verification token");
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public TokenRefreshResponse refreshtoken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromEmail(user.getEmail());
                    Date expirationDate = jwtUtils.extractExpirationDate(token);

                    return new TokenRefreshResponse(token, requestRefreshToken, expirationDate);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in the database!"));
    }

    public List<UserDto> findAll() {

        return userRepository.findAll()
                .stream()
                .map(INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    public UserDto findById(Long id) {

        return userRepository.findById(id)
                .map(INSTANCE::toDto)
                .orElseThrow(NoSuchElementException::new);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }


}
