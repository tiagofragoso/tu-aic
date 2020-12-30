package group3.aic_middleware.endpoints;

import group3.aic_middleware.exceptions.*;
import group3.aic_middleware.restData.*;
import group3.aic_middleware.services.FederationService;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * The Endpoint which handles the incoming requests from IoT devices and UI
 */
@Log4j
@RestController
@RequestMapping("/events")
public class GatewayController {

    FederationService federationService = new FederationService();

    public GatewayController() throws NoSuchAlgorithmException {
    }

    /**
     * The call which creates an event
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody SensingEventDTO sensingEvent) {
        log.info("Creating an event:");
        log.info(sensingEvent.toString());

        MetaDataServiceDTO metaData = new MetaDataServiceDTO();
        federationService.copyMetaDataFromEntityToDTO(metaData, sensingEvent.getMetaData());

        StoreEventDTO storeEventDTO = new StoreEventDTO();

        storeEventDTO.setImage(sensingEvent.getBase64EncodedImage());
        storeEventDTO.setMetadata(metaData);

        try{
            this.federationService.saveEvent(storeEventDTO);
        } catch (EventNotCreatedException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_MODIFIED, "Sensing event creation failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            log.warn(exceptionAsString);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }
    }

    /**
     * The call which queries an event
     */
    @GetMapping("/{seqId}")
    @ResponseStatus(HttpStatus.OK)
    public ReadDetailsEventDTO getById(@PathVariable("seqId") String seqId) { // 3
        log.info("Reading an event with the seqId = " + seqId);
        try {
            return this.federationService.readEvent(seqId);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sensing event reading failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            log.warn(exceptionAsString);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }
    }

    /**
     * The call which queries all events stored in the system
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MetadataPageDTO getEvents(@PageableDefault(size=20) Pageable pageable, @RequestParam(required = false) String search) {
        log.info("Reading all events.");
        try {
            return  this.federationService.readEvents(pageable, search);
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);
        }
    }

    /**
     * The call which queries all events stored in the system within a radius of specific longitude/latitude
     */
    @GetMapping("/radius")
    @ResponseStatus(HttpStatus.OK)
    public List<ReadEventsDTO> getEventsInRadius(@RequestParam double size, @RequestParam double lon, @RequestParam double lat) {
        log.info("Reading all events.");
        try {
            return  this.federationService.readEvents(size, lon, lat);
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);

        }
    }

    /**
     * The call which queries the data for a tag
     */
    @GetMapping("/{seqId}/tags/{tagName}")
    @ResponseStatus(HttpStatus.OK)
    public TagDataDTO getTagData(@PathVariable("seqId") String seqId, @PathVariable("seqId") String tagName) {
        log.info("Reading data for an event with seqId = " + seqId + " with a tag = " + tagName);
        try {
            return  this.federationService.readTagData(seqId, tagName);
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);

        }
    }

    /**
     * The call which updates an event
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody StoreEventDTO storeEventDTO) throws JSONException {

        log.info("Updating an event:");
        log.info(storeEventDTO.toString());

        try{
            this.federationService.updateEvent(storeEventDTO);
        } catch (EventNotUpdatedException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_MODIFIED, "Sensing event update failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            log.warn(exceptionAsString);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }
    }



    /**
     * The call which creates adds a new tag to the event
     */
    @PutMapping("/{seqId}/tags")
    @ResponseStatus(HttpStatus.OK)
    public void createTag(@RequestBody TagDataDTO tagDataDTO, @PathVariable String seqId) throws JSONException {
        log.info("Adding a tag to an event with seqId = " + seqId + ":");
        log.info(tagDataDTO.toString());

        try{
            this.federationService.createTag(tagDataDTO, seqId);
        } catch (EventNotUpdatedException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_MODIFIED, "Tag creation failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            log.warn(exceptionAsString);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }
    }

    /**
     * The call which cascade deletes an event
     */
    @DeleteMapping("/{seqId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("seqId") String seqId) {
        log.info("Deleting an event with the seqId = " + seqId);
        try {
            this.federationService.deleteEvent(seqId);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sensing event deletion failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }
    }

    /**
     * The call which deletes a tag of an event and corresponding images from primary and backup storage
     */
    @DeleteMapping("/{seqId}/tags/{tagName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTag(@PathVariable("seqId") String seqId, @PathVariable("tagName") String tagName) {
        log.info("Deleting a tag: " + tagName+ " for an event with the seqId = " + seqId);
        try {
            this.federationService.deleteTag(seqId, tagName);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Sensing event deletion failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }
    }

}
