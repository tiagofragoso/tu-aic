package com.example.MetadataService.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.stream.Collectors;


@NoArgsConstructor
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

    @Getter
    @Setter
    private String createdHumanReadable;

    @Getter
    @Setter
    private String updatedHumanReadable;

    private double longitude;
    private double latitude;

    @GeoSpatialIndexed
    private double[] gpsLocation = new double[2];

    @Getter
    @Setter
    private long updated;

    @Getter
    @Setter
    private String tagConcat;

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

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        updateTagsConcatString();
    }

    public SensingEvent(String id, String name, String deviceIdentifier, long timestamp, List<Tag> tags, double longitude, double latitude, long frameNum, String placeIdent, long eventFrames, long updated) {
        this.id = id;
        this.name = name;
        this.deviceIdentifier = deviceIdentifier;
        this.timestamp = timestamp;
        this.tags = tags;
        this.frameNum = frameNum;
        this.placeIdent = placeIdent;
        this.eventFrames = eventFrames;
        this.setLongitude(longitude);
        this.setLatitude(latitude);
        this.updated = updated;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm").withZone(ZoneId.systemDefault()).withLocale(Locale.getDefault());

        this.createdHumanReadable = formatter.format(Instant.ofEpochSecond(timestamp));
        this.updatedHumanReadable = formatter.format(Instant.ofEpochSecond(timestamp));

        updateTagsConcatString();
    }

    public void updateTagsConcatString() {
        this.tagConcat = tags.stream().map(Tag::getTagName).collect(Collectors.joining(","));
    }
}
