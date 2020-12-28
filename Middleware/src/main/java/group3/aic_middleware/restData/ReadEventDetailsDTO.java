package group3.aic_middleware.restData;

import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.entities.MetaDataEntity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadEventDetailsDTO {

    @Getter
    @Setter
    private String imageBase64Enc;

    @Getter
    @Setter
    private MetaDataEntity metaData;

}
