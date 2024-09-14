package UDD.AleksaColovic.SearchEngine.controller;

import UDD.AleksaColovic.SearchEngine.dto.LoginDTO;
import UDD.AleksaColovic.SearchEngine.dto.RegisterDTO;
import UDD.AleksaColovic.SearchEngine.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterDTO dto) {
        try {
            String token = userService.register(dto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(token);
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginDTO data) {
        try {
            String token = userService.login(data);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(token);
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }
}
