package group3.aic_middleware.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import group3.aic_middleware.restData.TagDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataEntity {

    @Getter
    @Setter
    // "place_ident": "031909GRAS01"
    @JsonProperty(required = true, value = "place_ident")
    private String placeIdent;

    @Getter
    @Setter
    // "name": "GRASMERE 1"
    @JsonProperty(required = true, value = "name")
    private String name;

    @Getter
    @Setter
    // "seq_id": "6ea10ab8-2e32-11e9-b03f-dca9047ef277"
    @JsonProperty(required = true, value = "seq_id")
    private String seqId;

    @Getter
    @Setter
    // "longitude": -115.9043718414795
    @JsonProperty(required = true, value = "longitude")
    private double longitude;

    @Getter
    @Setter
    // "datetime": "22-Apr-2019 (00:53:00.000000)"
    @JsonProperty(required = true, value = "datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy '('HH:mm:ss.SSSSSS')'")
    private LocalDateTime datetime;

    @Getter
    @Setter
    // "frame_num": 1
    @JsonProperty(required = true, value = "frame_num")
    private long frameNum;

    @Getter
    @Setter
    // "seq_num_frames": 1
    @JsonProperty(required = true, value = "seq_num_frames")
    private long seqNumFrames;

    @Getter
    @Setter
    // "latitude": 42.38461654217346
    @JsonProperty(required = true, value = "latitude")
    private double latitude;

    @Getter
    @Setter
    // "filename": "0a914caf-2bfa-11e9-bcad-06f10d5896c4.jpg"
    @JsonProperty(value = "filename")
    private String filename;

    @Getter
    @Setter
    // "device_id": "b3f129b8-59f2-458f-bf2f-f0c1af0032d3"
    @JsonProperty(required = true, value = "device_id")
    private String deviceId;

    @Getter
    @Setter
    @JsonProperty(required = false, value="tags")
    private List<TagDTO> tags;
}
