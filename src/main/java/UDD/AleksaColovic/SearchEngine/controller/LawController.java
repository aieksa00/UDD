package UDD.AleksaColovic.SearchEngine.controller;

import UDD.AleksaColovic.SearchEngine.converter.LawConverter;
import UDD.AleksaColovic.SearchEngine.converter.SearchConverter;
import UDD.AleksaColovic.SearchEngine.dto.LawDTO;
import UDD.AleksaColovic.SearchEngine.dto.SearchDTO;
import UDD.AleksaColovic.SearchEngine.service.LawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/law")
@RequiredArgsConstructor
public class LawController {
    //region: Fields
    private final LawService lawService;
    private final LawConverter lawConverter;
    private final SearchConverter searchConverter;
    //endregion

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") final MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Trying to upload an empty file: %s", file.getOriginalFilename()));
        }

        try {
            lawService.upload(file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Law uploaded successfully!");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable final String id) {
        try {
            UUID uuid = UUID.fromString(id);
            LawDTO dto = lawConverter.toDTO(lawService.findById(uuid));

            if(dto == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(String.format("Law with the given id: [%s] does not exists", id));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<LawDTO> dtos = new ArrayList<>();
        try {
            lawService.findAll().forEach(lawDocument -> dtos.add(lawConverter.toDTO(lawDocument)));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(dtos);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @PutMapping("/search")
    public ResponseEntity<?> search(@RequestBody final SearchDTO dto) {
        try {
            var hits = lawService.search(searchConverter.createSearchItems(dto), dto.getRadius());
            List<LawDTO> dtos = searchConverter.convertToLawDTOs(hits);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        try {
            UUID uuid = UUID.fromString(id);
            lawService.delete(uuid);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Law deleted successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }
}
