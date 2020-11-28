package group3.aic_middleware.endpoints;

import group3.aic_middleware.exceptions.DeviceNotFoundException;
import group3.aic_middleware.exceptions.DropboxLoginException;
import group3.aic_middleware.exceptions.ImageNotCreatedException;
import group3.aic_middleware.exceptions.ImageNotFoundException;
import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.restData.ImageObjectDTO;
import group3.aic_middleware.restData.SensingEventDTO;
import group3.aic_middleware.services.FederationService;
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
@RestController
@RequestMapping("/images")
public class GatewayController {

    FederationService federationService = new FederationService();

    public GatewayController() throws NoSuchAlgorithmException, ImageNotFoundException, ImageNotCreatedException, DropboxLoginException, JSONException, IOException {
    }

    /**
     * The call which creates an image
     */
    @PostMapping("/{sensingEvent}")
    @ResponseStatus(HttpStatus.CREATED) // 8
    public void create(@PathVariable("sensingEvent") SensingEventDTO sensingEvent){
        ImageObjectDTO entity = new ImageObjectDTO();
        ImageEntity image = new ImageEntity(sensingEvent.getBase64EncodedImage());

        entity.setImage(image);
        entity.setMetaData(sensingEvent.getMetaData());

        try{
            this.federationService.saveImage(entity);
        } catch (ImageNotCreatedException e) {
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
    public ImageObjectDTO getById(@PathVariable("seqId") String seqId) { // 3
        try {
            return  this.federationService.readImage(seqId);
        } catch (ImageNotFoundException e) {
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
    public List<ImageObjectDTO> getByDeviceId(@PathVariable("id") String id) { // 3
        try {
            return  this.federationService.readImagesForDevice(id);
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
        try {
            this.federationService.deleteImage(seqId);
        } catch (ImageNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Image deletion failed. Reason: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", e);
        }

    }

}
