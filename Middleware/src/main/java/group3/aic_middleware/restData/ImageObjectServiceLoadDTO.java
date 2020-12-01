package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageObjectServiceLoadDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "image_file")
    private String base64Image;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "last_modified")
    private LocalDateTime lastModified;

}
