package com.example.MetadataService.Endpoints;

import com.example.MetadataService.DTOs.EventDTO;
import com.example.MetadataService.Services.CRUDService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j
@RestController
public class EventEndpoint {
    private final CRUDService crudService;

    @Autowired
    public EventEndpoint(CRUDService crudService) {
        this.crudService = crudService;
    }

    @PostMapping("events")
    public ResponseEntity createEvent(@RequestBody EventDTO event) {
        log.info("Create event");
        crudService.createEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("events/{eventId}")
    public EventDTO getEventDetails(@PathVariable String eventId) throws Exception {
        log.info(String.format("Get event details for %s", eventId));
        return crudService.getEventDetails(eventId);
    }

    @GetMapping("events")
    public List<EventDTO> getAllEvents() throws Exception {
        log.info("Get all events.");
        return crudService.getAllEvents();
    }

    @GetMapping("events/radius")
    public List<EventDTO> getAllEventsInRadius(@RequestParam(required = true) double size, @RequestParam(required = true) double lon, @RequestParam(required = true) double lat) throws Exception {
        log.info(String.format("Get all events in radius size: %.2f  lon: %.2f lat: %.2f", size, lon, lat));
        return crudService.getAllEventsInRadius(size, lon, lat);
    }
}
