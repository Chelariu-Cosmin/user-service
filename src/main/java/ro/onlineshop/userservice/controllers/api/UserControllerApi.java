package ro.onlineshop.userservice.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.web.bind.annotation.*;
import ro.onlineshop.api.beans.UserDto;
import ro.onlineshop.api.payload.request.LoginRequest;
import ro.onlineshop.api.payload.request.TokenRefreshRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping("/users")
@Tag(name = "User authentication", description = "It exposes user authentication and CRUD operations")
public interface UserControllerApi {

    @Operation(summary = "Show all Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation executed successfully"
            ),
            @ApiResponse(responseCode = "500", description = "If the server has encountered a situation it does not know how to handle", content = {@Content(schema = @Schema())})
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    ResponseEntity<List<UserDto>> findAll();

    @Operation(
            summary = "Retrieve a User by Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operation executed successfully", content = {@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "UserDto with giving id not found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "The server will not process the request due to invalid path variable", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, the server has encountered a situation it does not know how to handle", content = {@Content(schema = @Schema())})})
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    ResponseEntity<UserDto> findById(@PathVariable("id") Long id);

    @Operation(
            summary = "Saves a given entity",
            description = "Saves a given entity and assigns authorized resources to access it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operation executed successfully", content = {@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "The server will not process the request due to invalid path variable", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, the server has encountered a situation it does not know how to handle", content = {@Content(schema = @Schema())})})
    @PostMapping("/signup")
    ResponseEntity<?> registerUser(@Valid @RequestBody UserDto signUpRequest, final HttpServletRequest request);


    @Operation(
            summary = "Saves a given entity",
            description = "Saves a given entity and assigns authorized resources to access it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operation executed successfully", content = {@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "The server will not process the request due to invalid path variable", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, the server has encountered a situation it does not know how to handle", content = {@Content(schema = @Schema())})})
    @PostMapping("/oauth2s")
    ResponseEntity<?> loginUserGoogle(@Valid @RequestBody OidcUserRequest userInfo);

    @Operation(
            summary = "verifyEmail",
            description = "verifyEmail.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operation executed successfully", content = {@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "The server will not process the request due to invalid path variable", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, the server has encountered a situation it does not know how to handle", content = {@Content(schema = @Schema())})})
    @PostMapping("/verifyEmail")
    ResponseEntity<?> verifyEmail(@RequestParam("token") String token);

    @Operation(
            summary = "Authenticate User",
            description = "Authenticates a user with the provided login credentials."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/signin")
    ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest);

    @Operation(
            summary = "Refresh Token",
            description = "Refreshes an expired access token using a refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refresh successful", content = {@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refreshtoken")
    ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request);

    @Operation(
            summary = "Delete User by ID",
            description = "Deletes a user with the provided ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User with given ID not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<Object> deleteById(@PathVariable("id") @NotNull Long id);
}
