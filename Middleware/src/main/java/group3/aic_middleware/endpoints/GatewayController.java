package group3.aic_middleware.endpoints;

import group3.aic_middleware.exceptions.DeviceNotFoundException;
import group3.aic_middleware.exceptions.DropboxLoginException;
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
        log.info("Creating an event");
        log.info(sensingEvent.toString());
        log.warn("c1");

        ReadEventDTO readEventDTO = new ReadEventDTO();
        ImageEntity image = new ImageEntity(sensingEvent.getBase64EncodedImage());
        log.warn("c2");

        readEventDTO.setImage(image);
        log.warn("c3");
        readEventDTO.setMetaData(sensingEvent.getMetaData());
        log.warn("c4");

        try{
            log.warn("c5");
            this.federationService.saveEvent(readEventDTO);
        } catch (EventNotCreatedException e) {
            log.warn("c6");
            throw new ResponseStatusException(
                    HttpStatus.NOT_MODIFIED, "Sensing event creation failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            log.warn("c7");
            log.warn(e.getMessage());
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
     * The call which queries events for a device
     */
    @GetMapping("/device/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReadEventDTO> getByDeviceId(@PathVariable("id") String id) { // 3
        log.info("Reading events for a device with the id = " + id);
        try {
            return  this.federationService.readEventsForDevice(id);
        } catch (DeviceNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sensing events for this device were not found. Reason: " + e.getMessage());
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);

        }
    }

    // TODO: update

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
            //log.error(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sensing event deletion failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            //log.error(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }

    }

}
