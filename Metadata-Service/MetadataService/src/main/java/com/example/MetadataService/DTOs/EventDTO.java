package com.example.MetadataService.DTOs;

import com.example.MetadataService.Entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventDTO {
    private String sensingEventId = null;
    private String name;
    private String deviceIdentifier;
    private long timestamp;
    private List<TagDTO> tags;
    private double longitude;
    private double latitude;

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
