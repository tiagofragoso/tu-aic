package group3.aic_middleware.restData;

import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.entities.MetaDataEntity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageObjectDTO {

    @Getter
    @Setter
    // Pronounced as sweet
    private long seuid;

    @Getter
    @Setter
    private ImageEntity image;

    @Getter
    @Setter
    private MetaDataEntity metaData;

}
