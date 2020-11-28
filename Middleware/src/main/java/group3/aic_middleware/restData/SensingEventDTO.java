package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import group3.aic_middleware.entities.MetaDataEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class SensingEventDTO {

    @Getter
    @Setter
    @JsonProperty(required = true, value = "image")
    private String base64EncodedImage;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "metadata")
    private MetaDataEntity metaData;
}
