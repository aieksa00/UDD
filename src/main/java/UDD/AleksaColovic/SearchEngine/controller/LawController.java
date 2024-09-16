package UDD.AleksaColovic.SearchEngine.controller;

import UDD.AleksaColovic.SearchEngine.controller.responses.GetPageOfContractsResponse;
import UDD.AleksaColovic.SearchEngine.controller.responses.GetPageOfLawsResponse;
import UDD.AleksaColovic.SearchEngine.converter.LawConverter;
import UDD.AleksaColovic.SearchEngine.converter.SearchConverter;
import UDD.AleksaColovic.SearchEngine.dto.LawDTO;
import UDD.AleksaColovic.SearchEngine.dto.SearchDTO;
import UDD.AleksaColovic.SearchEngine.model.LawDocument;
import UDD.AleksaColovic.SearchEngine.service.LawService;
import UDD.AleksaColovic.SearchEngine.service.common.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/law")
@RequiredArgsConstructor
public class LawController {
    //region: Fields
    private final MinioService minioService;

    private final LawService lawService;
    private final LawConverter lawConverter;
    private final SearchConverter searchConverter;
    //endregion

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody final MultipartFile file) {
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

    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam final String fileName) {
        try {
            var response = minioService.loadFile(fileName, "laws");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestParam final int page, @RequestParam final int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<LawDocument> pageContracts = lawService.findAll(pageable);
            Page<LawDTO> dtoPage = pageContracts.map(lawConverter::toDTO);

            GetPageOfLawsResponse response = GetPageOfLawsResponse.builder()
                    .laws(dtoPage.stream().toList())
                    .pageSize(dtoPage.getSize())
                    .currentPage(page+1)
                    .totalPages(dtoPage.getTotalPages())
                    .totalItems(dtoPage.getNumberOfElements())
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @PutMapping("/search")
    public ResponseEntity<?> search(@RequestBody final SearchDTO dto, @RequestParam final int page, @RequestParam final int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            var hits = lawService.search(searchConverter.createSearchItems(dto), dto.getRadius(), pageable);
            List<LawDTO> dtos = searchConverter.convertToLawDTOs(hits);

            GetPageOfLawsResponse response = GetPageOfLawsResponse.builder()
                    .laws(dtos)
                    .pageSize(size)
                    .currentPage(page+1)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam final String id) {
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
