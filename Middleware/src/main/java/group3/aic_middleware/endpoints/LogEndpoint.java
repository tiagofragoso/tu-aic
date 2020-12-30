package group3.aic_middleware.endpoints;

import group3.aic_middleware.restData.LogDTO;
import group3.aic_middleware.restData.ReadEventsDTO;
import group3.aic_middleware.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogEndpoint {

    @Autowired
    LogService logService;


    /**
     * The call which queries the last 'logCount' logs
     *
     * @param logCount: The last logs according to this number.
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<LogDTO> getEventsInRadius(@RequestParam(defaultValue = "20") int count) {
        try {
            return logService.getAllLogs(count);
        } catch (Exception exc) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", exc);
        }
    }
}
