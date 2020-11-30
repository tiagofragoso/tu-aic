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
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * The Endpoint which handles stuff
 */
@Log4j
@RestController
@RequestMapping("/events")
public class GatewayController {

    FederationService federationService = new FederationService();

    public GatewayController() throws NoSuchAlgorithmException, EventNotFoundException, EventNotCreatedException, DropboxLoginException, JSONException, IOException {
    }

    /**
     * The call which creates an image
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 8
    public void create(@RequestBody SensingEventDTO sensingEvent){
        log.info("creating an event");
        ReadEventDTO entity = new ReadEventDTO();
        ImageEntity image = new ImageEntity(sensingEvent.getBase64EncodedImage());

        entity.setImage(image);
        entity.setMetaData(sensingEvent.getMetaData());

        try{
            this.federationService.saveEvent(entity);
        } catch (EventNotCreatedException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_MODIFIED, "Image creation failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }
    }

    /**
     * The call which queries an image
     */
    @GetMapping("/{seqId}")
    @ResponseStatus(HttpStatus.OK) //5
    public ReadEventDTO getById(@PathVariable("seqId") String seqId) { // 3
        log.info("reading an event");
        try {
            return  this.federationService.readEvent(seqId);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Image reading failed. Reason: " + e.getMessage());
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);
        }
    }

    /**
     * The call which queries images for a device
     */
    @GetMapping("/device/{id}")
    @ResponseStatus(HttpStatus.OK) //5
    public List<ReadEventDTO> getByDeviceId(@PathVariable("id") String id) { // 3
        log.info("reading events for a device");
        try {
            return  this.federationService.readEventsForDevice(id);
        } catch (DeviceNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Images for this device were not found. Reason: " + e.getMessage());
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);

        }
    }

    // TODO: update

    /**
     * The call which deletes an image
     */
    @DeleteMapping("/{seqId}")
    @ResponseStatus(HttpStatus.OK) // 8
    public void delete(@PathVariable("seqId") String seqId) {
        log.info("deleting an event");
        try {
            this.federationService.deleteEvent(seqId);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Image deletion failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }

    }

}
