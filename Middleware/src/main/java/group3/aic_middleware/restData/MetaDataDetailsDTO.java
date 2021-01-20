package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Iterator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataDetailsDTO {
    @Getter
    @Setter
    @JsonProperty(required = true, value = "event_id")
    private String sensingEventId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "name")
    private String name;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "dev_id")
    private String deviceIdentifier;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "created")
    private long timestamp;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "longitude")
    private double longitude;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "latitude")
    private double latitude;

    @Getter
    @Setter
    @JsonProperty(required = false, value="frame_num")
    private long frameNum;

    @Getter
    @Setter
    @JsonProperty(required = false, value="place_ident")
    private String placeIdent;

    @Getter
    @Setter
    @JsonProperty(required = false, value="event_frames")
    private long eventFrames;

    @Getter
    @Setter
    @JsonProperty(required = true, value="updated")
    private long updated;
}
