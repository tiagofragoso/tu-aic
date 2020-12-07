package group3.aic_middleware.endpoints;

import group3.aic_middleware.exceptions.DropboxLoginException;
import group3.aic_middleware.exceptions.DuplicateEventException;
import group3.aic_middleware.exceptions.EventNotCreatedException;
import group3.aic_middleware.exceptions.EventNotFoundException;
import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.restData.ReadEventDTO;
import group3.aic_middleware.restData.SensingEventDTO;
import group3.aic_middleware.services.FederationService;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    public GatewayController() throws NoSuchAlgorithmException, EventNotFoundException, EventNotCreatedException, DropboxLoginException, JSONException, IOException {
    }

    /**
     * The call which creates an event
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody SensingEventDTO sensingEvent) throws JSONException {
        log.info("Creating an event:");
        log.info(sensingEvent.toString());

        ReadEventDTO readEventDTO = new ReadEventDTO();
        ImageEntity image = new ImageEntity(sensingEvent.getBase64EncodedImage());

        readEventDTO.setImage(image);
        readEventDTO.setMetaData(sensingEvent.getMetaData());

        try{
            this.federationService.saveEvent(readEventDTO);
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
    public ReadEventDTO getById(@PathVariable("seqId") String seqId) { // 3
        log.info("Reading an event with the seqId = " + seqId);
        try {
            return  this.federationService.readEvent(seqId);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sensing event reading failed. Reason: " + e.getMessage());
        } catch (DuplicateEventException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Sensing event reading failed. Reason: " + e.getMessage());
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
    public List<ReadEventDTO> getEvents() {
        log.info("Reading all events.");
        try {
            return  this.federationService.readEvents();
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);

        }
    }

    // TODO Stage 2: update

    /**
     * The call which deletes an event
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

}
