package group3.aic_middleware.restData;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageObjectEntity {
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
