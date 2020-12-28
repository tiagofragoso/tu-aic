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

    @Override
    public String toString() {
        return "seqId: " + this.metaData.getSeqId() + "\n"
                + "deviceId: " + this.metaData.getDeviceId() + "\n"
                + "name: " + this.metaData.getName() + "\n"
                + "filename: " + this.metaData.getFilename() + "\n"
                + "placeIdent: " + this.metaData.getPlaceIdent() + "\n"
                + "latitude: " + this.metaData.getLatitude() + "\n"
                + "longitude: " + this.metaData.getLongitude() + "\n"
                + "frameNum: " + this.metaData.getFrameNum() + "\n"
                + "seqFrameNum: " + this.metaData.getSeqNumFrames() + "\n"
                + "datetime: " + this.metaData.getDatetime();
    }

}
