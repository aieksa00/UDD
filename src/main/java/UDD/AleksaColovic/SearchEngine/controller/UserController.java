package UDD.AleksaColovic.SearchEngine.controller;

import UDD.AleksaColovic.SearchEngine.dto.LoginDTO;
import UDD.AleksaColovic.SearchEngine.dto.RegisterDTO;
import UDD.AleksaColovic.SearchEngine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> register (@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(userService.login(dto));
    }
}
