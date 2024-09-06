package UDD.AleksaColovic.SearchEngine.service;

import UDD.AleksaColovic.SearchEngine.dto.LoginDTO;
import UDD.AleksaColovic.SearchEngine.dto.RegisterDTO;
import UDD.AleksaColovic.SearchEngine.model.Role;
import UDD.AleksaColovic.SearchEngine.model.User;
import UDD.AleksaColovic.SearchEngine.repository.UserRepository;
import UDD.AleksaColovic.SearchEngine.service.common.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterDTO dto) {
        if (dto == null) {
            return null;
        }

        var user = User
                .builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public String login(LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        var user = userRepository.findByEmail(dto.getEmail()).orElseThrow();

        return jwtService.generateToken(user);
    }
}
