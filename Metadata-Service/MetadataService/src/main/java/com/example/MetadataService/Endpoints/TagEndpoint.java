package com.example.MetadataService.Endpoints;

import com.example.MetadataService.DTOs.TagDTO;
import com.example.MetadataService.Services.CRUDService;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
public class TagEndpoint {

    private final CRUDService crudService;

    @Autowired
    public TagEndpoint(CRUDService crudService) {
        this.crudService = crudService;
    }

    @PostMapping("events/{eventId}/tags")
    public ResponseEntity addTag(@PathVariable String eventId, @RequestBody TagDTO tag) {

        log.info(String.format("Add Tag: "));
        crudService.addTag(eventId, tag);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("events/{eventId}/tags")
    public ResponseEntity updateTag(@PathVariable String eventId, @RequestBody TagDTO tag) {
        log.info(String.format("Update Tag: "));
        crudService.updateTag(eventId, tag);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
