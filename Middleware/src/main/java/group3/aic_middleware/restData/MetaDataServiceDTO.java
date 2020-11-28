package group3.aic_middleware.restData;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataServiceDTO {

    @Getter
    @Setter
    private String sensingEventId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String deviceIdentifier;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private double longitude;

    @Getter
    @Setter
    private double latitude;

    @Getter
    @Setter
    private List<TagDTO> tags;

}
