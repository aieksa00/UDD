package UDD.AleksaColovic.SearchEngine.controller;

import UDD.AleksaColovic.SearchEngine.service.common.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/recreate")
    public ResponseEntity<String> recreateIndices() {
        Set<String> recreatedIndexes = indexService.recreateIndices();
        String message = String.format("%d indexes recreated successfully! %s",
                recreatedIndexes.size(),
                recreatedIndexes);

        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }

}
