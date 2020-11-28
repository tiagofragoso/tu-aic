package com.example.MetadataService.DTOs;

import com.example.MetadataService.Entities.Tag;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {

    @JsonProperty(required = true, value = "event_id")
    private String sensingEventId = null;

    @JsonProperty(required = true, value = "name")
    private String name;

    @JsonProperty(required = true, value = "dev_id")
    private String deviceIdentifier;

    @JsonProperty(required = true, value = "timestamp")
    private long timestamp;

    @JsonProperty(required = true, value = "longitude")
    private double longitude;

    @JsonProperty(required = true, value = "latitude")
    private double latitude;

    @JsonProperty(required = false, value="tags")
    private List<TagDTO> tags;

    @JsonProperty(required = false, value="frame_num")
    private long frameNum;

    @JsonProperty(required = false, value="place_ident")
    private String placeIdent;

    @JsonProperty(required = false, value="event_frames")
    private long eventFrames;

    /**
     * Ctor to create a new event.
     * @param deviceIdentifier the device id
     * @param timestamp the timestamp of the capture
     * @param longitude the longitude
     * @param latitude the latitude
     */
    public EventDTO(String deviceIdentifier, String name, long timestamp, double longitude, double latitude) {
        this.deviceIdentifier = deviceIdentifier;
        this.timestamp = timestamp;
        this.tags = tags;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
