package ro.onlineshop.userservice.services.authentification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ro.onlineshop.api.beans.ERole;
import ro.onlineshop.api.beans.LoginProviderDto;
import ro.onlineshop.api.payload.request.GoogleOAuth2UserInfo;
import ro.onlineshop.userservice.entities.Role;
import ro.onlineshop.userservice.entities.User;
import ro.onlineshop.userservice.repositories.RoleRepository;
import ro.onlineshop.userservice.repositories.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map attributes = oidcUser.getAttributes();
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo();
        userInfo.setEmail((String) attributes.get("email"));
        userInfo.setId((String) attributes.get("sub"));
        userInfo.setFirstName((String) attributes.get("firstName"));
        userInfo.setLastName((String) attributes.get("lastName"));
        updateUser(userInfo);
        return oidcUser;
    }

    private void updateUser(GoogleOAuth2UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail()).get();
        if(user == null) {
            user = new User();
        }
        user.setEmail(userInfo.getEmail());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setProvider(LoginProviderDto.GOOGLE);
        user.setRoles(getDefaultUserRole());
        userRepository.save(user);
    }

    private Set<Role> getDefaultUserRole() {
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Default role not found."));

        return new HashSet<>(Collections.singletonList(userRole));
    }
}