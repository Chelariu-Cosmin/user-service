package ro.onlineshop.userservice.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ro.onlineshop.api.beans.ERole;
import ro.onlineshop.api.beans.UserDto;
import ro.onlineshop.userservice.entities.Role;
import ro.onlineshop.userservice.entities.User;
import ro.onlineshop.userservice.repositories.RoleRepository;
import ro.onlineshop.userservice.utils.context.SpringContextProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    @Named("roleNamesToRoles")
    default Set<Role> roleNamesToRoles(Set<String> roles) {
        if (roles == null) {
            return null;
        }

        ApplicationContext context = SpringContextProvider.getApplicationContext();
        RoleRepository roleRepository = context.getBean(RoleRepository.class);

        Set<Role> userRoles = new HashSet<>();
        roles.forEach(roleName -> {
            switch (roleName) {
                case "admin" -> {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    userRoles.add(adminRole);
                }
                case "mod" -> {
                    Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    userRoles.add(modRole);
                }
                default -> {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    userRoles.add(userRole);
                }
            }
        });
        return userRoles;
    }


    @Named("rolesToRoleNames")
    default Set<String> rolesToRoleNames(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }


    @Named("encodePassword")
    default String encodePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    @InheritInverseConfiguration
    @Mapping(source = "roles", target = "role", qualifiedByName = "rolesToRoleNames")
    UserDto toDto(User user);

    @Mapping(source = "role", target = "roles", qualifiedByName = "roleNamesToRoles")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    User toUser(UserDto userDto);
}


