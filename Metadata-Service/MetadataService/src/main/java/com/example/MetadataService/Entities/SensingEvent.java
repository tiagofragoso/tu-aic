package com.example.MetadataService.Entities;

import com.example.MetadataService.DTOs.EventDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@CompoundIndex(name="unique_tag_name", def = "{ 'tag.tagName' : 1 }", unique = true)
@Document
public class SensingEvent {


    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String deviceIdentifier;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private List<Tag> tags;

    @Getter
    @Setter
    private long frameNum;

    @Getter
    @Setter
    private String placeIdent;

    @Getter
    @Setter
    private long eventFrames;

    private double longitude;
    private double latitude;

    @GeoSpatialIndexed
    private double[] gpsLocation = new double[2];

    public double getLongitude() throws Exception{
        if(gpsLocation.length > 0) {
            return gpsLocation[0];
        }
        throw new Exception("No Coordination information available!");
    }

    public double getLatitude() throws Exception {
        if(gpsLocation.length > 0) {
            return gpsLocation[1];
        }
        throw new Exception("No Coordination information available!");
    }

    public void setLongitude(double longitude) {
        gpsLocation[0] = longitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        gpsLocation[1] = latitude;
        this.latitude = latitude;
    }

    public SensingEvent(String id, String name, String deviceIdentifier, long timestamp, List<Tag> tags, double longitude, double latitude, long frameNum, String placeIdent, long eventFrames) {
        this.id = id;
        this.name = name;
        this.deviceIdentifier = deviceIdentifier;
        this.timestamp = timestamp;
        this.tags = tags;
        this.setLongitude(longitude);
        this.setLatitude(latitude);
    }
}
