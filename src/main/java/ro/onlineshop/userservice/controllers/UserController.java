package ro.onlineshop.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.onlineshop.api.beans.UserDto;
import ro.onlineshop.api.payload.request.GoogleOAuth2UserInfo;
import ro.onlineshop.api.payload.request.LoginRequest;
import ro.onlineshop.api.payload.request.TokenRefreshRequest;
import ro.onlineshop.userservice.controllers.api.UserControllerApi;
import ro.onlineshop.userservice.services.UserService;
import ro.onlineshop.userservice.services.authentification.CustomOidcUserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController implements UserControllerApi {

    private final UserService userService;

    private final CustomOidcUserService customOidcUserService;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {

        try {
            return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id") @NotNull Long id) {

        try {
            return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
        } catch (InvalidDataAccessApiUsageException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto signUpRequest, final HttpServletRequest request) {
        try {
            this.userService.registerUser(signUpRequest, request);
            String successMessage = "Success! Please, check your email to complete your registration";
            return ResponseEntity.ok(successMessage);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMostSpecificCause().getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, throwable.getCause().getMessage());
        }
    }

    @PostMapping("/oauth2")
    public ResponseEntity<String> loginUserGoogle(@Valid @RequestBody OidcUserRequest userInfo) {
        try {
            this.customOidcUserService.loadUser(userInfo);
            String successMessage = "it works!";
            return ResponseEntity.ok(successMessage);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMostSpecificCause().getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, throwable.getCause().getMessage());
        }
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            return new ResponseEntity<>(userService.verifyEmail(token), HttpStatus.OK);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMostSpecificCause().getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, throwable.getCause().getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            return new ResponseEntity<>(userService.authenticateUser(loginRequest), HttpStatus.OK);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMostSpecificCause().getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, throwable.getCause().getMessage());
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {

        try {
            return new ResponseEntity<>(userService.refreshtoken(request), HttpStatus.OK);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMostSpecificCause().getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, throwable.getCause().getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable("id") @NotNull Long id) {

        try {
            userService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidDataAccessApiUsageException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
        }
    }
}
