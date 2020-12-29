package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ReadEventsDTO {

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
    @JsonProperty(required = true, value = "event_id")
    private String seqId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "created")
    private long created;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "updated")
    private long updated;

    @Getter
    @Setter
    // "longitude": -115.9043718414795
    @JsonProperty(required = true, value = "longitude")
    private double longitude;

    @Getter
    @Setter
    // "latitude": 42.38461654217346
    @JsonProperty(required = true, value = "latitude")
    private double latitude;

    @Getter
    @Setter
    // valid / faulty / missing
    @JsonProperty(required = true, value = "state")
    private String state;

    @Getter
    @Setter
    @JsonProperty(required = false, value="tags")
    private List<TagDTO> tags;

}
