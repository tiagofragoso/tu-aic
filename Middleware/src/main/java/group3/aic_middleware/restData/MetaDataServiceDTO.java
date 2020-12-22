package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataServiceDTO {

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
    @JsonProperty(required = true, value = "timestamp")
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
    @JsonProperty(required = false, value="tags")
    private List<TagDTO> tags;

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

    public long getCreated(String tagName) {
        while(this.tags.iterator().hasNext()) {
            TagDTO tagDTO = this.tags.iterator().next();
            if(tagDTO.getTagName() == tagName) {
                return tagDTO.getCreated();
            }
        }
        return -1;
    }
}
