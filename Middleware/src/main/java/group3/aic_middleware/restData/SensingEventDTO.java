package group3.aic_middleware.restData;

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
    private String base64EncodedImage;

    @Getter
    @Setter
    private MetaDataEntity metaData;

}
