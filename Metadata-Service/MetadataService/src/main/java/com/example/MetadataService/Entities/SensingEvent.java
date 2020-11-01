package com.example.MetadataService.Entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.List;


@Data
public class SensingEvent {

    @Id
    private String id;
    private String deviceIdentifier;
    private long timestamp;
    private List<Tag> tags;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private double longitude;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private double latitude;


    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
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

    public SensingEvent(String id, String deviceIdentifier, long timestamp, List<Tag> tags, double longitude, double latitude) {
        this.id = id;
        this.deviceIdentifier = deviceIdentifier;
        this.timestamp = timestamp;
        this.tags = tags;
        this.setLongitude(longitude);
        this.setLatitude(latitude);
    }
}
