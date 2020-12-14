package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "tag_name")
    private String tagName;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "image_hash")
    private int imageHash;

}
