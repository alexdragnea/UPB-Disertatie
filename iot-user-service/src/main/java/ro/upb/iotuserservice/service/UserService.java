package ro.upb.iotuserservice.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ro.upb.iotuserservice.dto.MeDto;
import ro.upb.iotuserservice.dto.RegisterUserRequest;
import ro.upb.iotuserservice.dto.UserCredential;
import ro.upb.iotuserservice.dto.UserDto;
import ro.upb.iotuserservice.exception.EmailExistException;
import ro.upb.iotuserservice.exception.EmailNotFoundException;
import ro.upb.iotuserservice.model.User;
import ro.upb.iotuserservice.model.UserPrincipal;
import ro.upb.iotuserservice.repository.UserRepository;
import ro.upb.iotuserservice.util.JWTTokenProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ro.upb.iotuserservice.constants.SecurityConstant.AUTHORITIES;
import static ro.upb.iotuserservice.enums.Role.ROLE_USER;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = findUserByEmail(email);
        validateLoginAttempt(user);
        userRepository.save(user);
        return new UserPrincipal(user);
    }

    public void register(RegisterUserRequest request) {
        validateEmail(request.getEmail());
        User newUser = new User();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(ROLE_USER.name());
        newUser.setAuthorities(ROLE_USER.getAuthorities());
        userRepository.save(newUser);

    }

    public UserDto validateToken(String token) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeToken(token);
        String userId = decodedJWT.getClaim("userId").asString();
        String username =
                decodedJWT.getClaim("firstName").asString()
                        + " "
                        + decodedJWT.getClaim("lastName").asString();
        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(decodedJWT);
        return new UserDto(userId, authorities, username);
    }

    public User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("User could not found by id " + id));
    }

    public User findUserByEmail(String email) {
        return userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("No user found for email: " + email));
    }

    private void validateLoginAttempt(User user) {

        loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
    }

    private void validateEmail(String email) {
        Optional<User> userByNewEmail = userRepository.findUserByEmail(email);
        if (userByNewEmail.isPresent()) {
            throw new EmailExistException("Email already exists.");
        }
    }

    public UserCredential getUserCredentialsById(UUID id) {
        User user = userRepository.getById(id);
        return new UserCredential(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public MeDto getMe(String token) {
        DecodedJWT decodedJWT = jwtTokenProvider.decodeToken(token);

        List<String> roles = decodedJWT.getClaim(AUTHORITIES).asList(String.class);
        String userId = decodedJWT.getClaim("userId").asString();
        String firstName = decodedJWT.getClaim("firstName").asString();
        String lastName = decodedJWT.getClaim("lastName").asString();
        String email = decodedJWT.getClaim("email").asString();

        return MeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userId(userId)
                .roles(roles)
                .build();
    }

    @Transactional
    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }
}
