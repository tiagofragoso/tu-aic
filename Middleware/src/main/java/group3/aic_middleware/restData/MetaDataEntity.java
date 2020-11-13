package group3.aic_middleware.restData;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataEntity {

    @Getter
    @Setter
    // "place_ident": "031909GRAS01"
    private String placeIdent;

    @Getter
    @Setter
    // "name": "GRASMERE 1"
    private String name;

    @Getter
    @Setter
    // "seq_id": "6ea10ab8-2e32-11e9-b03f-dca9047ef277"
    private String seqId;

    @Getter
    @Setter
    // "longitude": -115.9043718414795
    private double longitude;

    @Getter
    @Setter
    // "datetime": "22-Apr-2019 (00:53:00.000000)"
    private LocalDateTime datetime;

    @Getter
    @Setter
    // "frame_num": 1
    private int frameNum;

    @Getter
    @Setter
    // "seq_num_frames": 1
    private int seqNumFrames;

    @Getter
    @Setter
    // "latitude": 42.38461654217346
    private double latitude;

    @Getter
    @Setter
    // "filename": "0a914caf-2bfa-11e9-bcad-06f10d5896c4.jpg"
    private String filename;

    @Getter
    @Setter
    // "device_id": "b3f129b8-59f2-458f-bf2f-f0c1af0032d3"
    private String deviceId;

}
