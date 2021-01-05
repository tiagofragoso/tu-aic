package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Id;

@Data
@NoArgsConstructor
public class LogDTO {
    @Getter
    @Setter
    @JsonProperty(required = true, value = "log")
    private String log;

    public LogDTO(String dated, String logger, String level, String message) {
        this.log = String.format("%s - %s: %s : '%s'", dated, logger, level, message);
    }
}