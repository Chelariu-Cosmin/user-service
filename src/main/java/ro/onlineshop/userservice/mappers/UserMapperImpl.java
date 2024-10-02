//package ro.onlineshop.userservice.mappers;
//
//import org.mapstruct.factory.Mappers;
//import org.springframework.context.ApplicationContext;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//import ro.onlineshop.userservice.entities.Role;
//import ro.onlineshop.userservice.repositories.RoleRepository;
//import ro.onlineshop.userservice.utils.context.SpringContextProvider;
//import ro.onlineshop.api.beans.ERole;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//@Component
//public class UserMapperImpl implements IUserMapper2 {
//
//    @Override
//    public Set<String> rolesToRoleNames(Set<Role> roles) {
//            if (roles == null) {
//                return null;
//            }
//            return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
//    }
//
//    @Override
//    public Set<Role> roleNamesToRoles(Set<String> roles) {
//        if (roles == null) {
//            return null;
//        }
//
//        ApplicationContext context = SpringContextProvider.getApplicationContext();
//        RoleRepository roleRepository = context.getBean(RoleRepository.class);
//
//        Set<Role> userRoles = new HashSet<>();
//        roles.forEach(roleName -> {
//            switch (roleName) {
//                case "admin" -> {
//                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    userRoles.add(adminRole);
//                }
//                case "mod" -> {
//                    Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    userRoles.add(modRole);
//                }
//                default -> {
//                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    userRoles.add(userRole);
//                }
//            }
//        });
//        return userRoles;
//    }
//
//    @Override
//    public String encodePassword(String password) {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        return encoder.encode(password);
//    }
//}
