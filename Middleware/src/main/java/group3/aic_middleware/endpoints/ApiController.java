package group3.aic_middleware.endpoints;

import group3.aic_middleware.restData.ImageObjectEntity;
import group3.aic_middleware.service.federationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Endpoint which handles stuff
 */
@RestController
@RequestMapping("/images")
public class ApiController {
    // TODO: Check if error messages are returned
    /**
     * The call which posts an image
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED) // 8
    public void create(){
    try{
        // TODO
    }
	    catch (Exception exc) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);
    }
    }

    /**
     * The call which posts an image
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) //5
    public ImageObjectEntity getById(@PathVariable("id") String id) { // 3
        try {
            return  federationService.readImage(id);
                                     // findById(id) // 4
                                     // .map(UserResource::new) // 5
                                     // .map(ResponseEntity::ok) // 6
                                     // .orElse(ResponseEntity.notFound().build()); // 7
        } catch (Exception exc) {
            // TODO: Add 404 Exception
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);

        }
    }

    // TODO: update

    /**
     * The call which posts an image
     */
    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK) // 8
    public void delete() {
        try{
            // TODO
        }
        catch (Exception exc) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);
        }
    }

}
