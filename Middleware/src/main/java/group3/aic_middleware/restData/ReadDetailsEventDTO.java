package group3.aic_middleware.restData;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadDetailsEventDTO
{
    @Getter
    @Setter
    private String image;

    @Getter
    @Setter
    private MetaDataDetailsDTO metadata;

    @Getter
    @Setter
    private List<EventDetailsTagDTO> tags;

    @Override
    public String toString() {
        return "seqId: " + this.metadata.getSensingEventId() + "\n"
                + "deviceId: " + this.metadata.getDeviceIdentifier() + "\n"
                + "name: " + this.metadata.getName() + "\n"
                + "placeIdent: " + this.metadata.getPlaceIdent() + "\n"
                + "latitude: " + this.metadata.getLatitude() + "\n"
                + "longitude: " + this.metadata.getLongitude() + "\n"
                + "frameNum: " + this.metadata.getFrameNum() + "\n"
                + "seqFrameNum: " + this.metadata.getFrameNum() + "\n"
                + "created: " + this.metadata.getTimestamp();
    }

}
