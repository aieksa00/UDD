package UDD.AleksaColovic.SearchEngine.controller;

import UDD.AleksaColovic.SearchEngine.dto.ContractDTO;
import UDD.AleksaColovic.SearchEngine.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody final ContractDTO dto) {
        service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Contract created successfully!");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") final MultipartFile file) {
        service.upload(file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Contract uploaded successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable final String id) {
        try {
            UUID uuid = UUID.fromString(id);
            ContractDTO dto = service.findById(uuid);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Contract with the given id does not exist.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<ContractDTO> dtos = service.findAll();

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        try {
            UUID uuid = UUID.fromString(id);
            service.delete(uuid);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Contract deleted successfully!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Provided id is bad.");
        }
    }
}
