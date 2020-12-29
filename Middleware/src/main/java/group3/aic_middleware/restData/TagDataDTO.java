package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDataDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "tag_name")
    private String tagName;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "image")
    private String image;

    @Getter
    @Setter
    @JsonProperty(required = false, value = "created")
    private long created;

}
