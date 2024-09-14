package UDD.AleksaColovic.SearchEngine.controller;

import UDD.AleksaColovic.SearchEngine.controller.responses.GetPageOfContractsResponse;
import UDD.AleksaColovic.SearchEngine.converter.ContractConverter;
import UDD.AleksaColovic.SearchEngine.converter.SearchConverter;
import UDD.AleksaColovic.SearchEngine.dto.ContractDTO;
import UDD.AleksaColovic.SearchEngine.dto.SearchDTO;
import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import UDD.AleksaColovic.SearchEngine.service.ContractService;
import UDD.AleksaColovic.SearchEngine.service.helpers.LocationHelper;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {
    //region: Fields
    private final ContractService service;
    private final ContractConverter contractConverter;
    private final SearchConverter searchConverter;
    //endregion

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") final MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Trying to upload an empty file: %s", file.getOriginalFilename()));
        }

        try {
            service.upload(file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Contract uploaded successfully!");
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
            ContractDTO dto = contractConverter.toDTO(service.findById(uuid));

            if(dto == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(String.format("Contract with the given id: [%s] does not exists", id));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestParam final int page, @RequestParam final int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<ContractDocument> pageContracts = service.findAll(pageable);
            Page<ContractDTO> dtoPage = pageContracts.map(contractConverter::toDTO);
            
            GetPageOfContractsResponse resposne = GetPageOfContractsResponse.builder()
                    .contracts(dtoPage.stream().toList())
                    .pageSize(size)
                    .currentPage(page+1)
                    .totalPages(dtoPage.getTotalPages())
                    .totalItems(dtoPage.getTotalPages()*size)
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(resposne);
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

            var hits = service.search(searchConverter.createSearchItems(dto), dto.getRadius(), pageable);
            List<ContractDTO> dtos = searchConverter.convertToContractDTOs(hits);

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
            service.delete(uuid);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Contract deleted successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("There was a problem: %s", e.getMessage()));
        }
    }
}
