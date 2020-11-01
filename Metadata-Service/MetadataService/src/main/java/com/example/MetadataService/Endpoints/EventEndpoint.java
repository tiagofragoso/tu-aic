package com.example.MetadataService.Endpoints;

import com.example.MetadataService.Services.CRUDService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
public class EventEndpoint {

    private final CRUDService crudService;

    @Autowired
    public EventEndpoint(CRUDService crudService) {
        this.crudService = crudService;
    }

    @PostMapping("events")
    public void createEvent() {
        log.info("Create event");
    }

    @GetMapping("events/{eventId}")
    public void getEventDetails(@PathVariable String eventId) {
        log.info(String.format("Get event details for %s", eventId));
    }

    @GetMapping("events")
    public void getAllEvents() {
        log.info("Get all events.");
    }

    @GetMapping("events/radius")
    public void getAllEventsInRadius(@RequestParam(required = true) double size, @RequestParam(required = true) double lon, @RequestParam(required = true) double lat) {
        log.info(String.format("Get all events in radius size: %.2f  lon: %.2f lat: %.2f", size, lon, lat));
    }
}
