package group3.aic_middleware.restData;

import group3.aic_middleware.entities.ImageEntity;
import group3.aic_middleware.entities.MetaDataEntity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreEventDTO {

    @Getter
    @Setter
    private String imageBase64Enc;

    @Getter
    @Setter
    private MetaDataServiceDTO metaData;

    @Override
    public String toString() {
        return "seqId: " + this.metaData.getSensingEventId() + "\n"
                + "deviceId: " + this.metaData.getDeviceIdentifier() + "\n"
                + "name: " + this.metaData.getName() + "\n"
                + "placeIdent: " + this.metaData.getPlaceIdent() + "\n"
                + "latitude: " + this.metaData.getLatitude() + "\n"
                + "longitude: " + this.metaData.getLongitude() + "\n"
                + "frameNum: " + this.metaData.getFrameNum() + "\n"
                + "seqFrameNum: " + this.metaData.getFrameNum() + "\n"
                + "created: " + this.metaData.getTimestamp();
    }

}
