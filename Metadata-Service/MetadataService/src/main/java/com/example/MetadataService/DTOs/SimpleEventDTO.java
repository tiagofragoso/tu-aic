package com.example.MetadataService.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleEventDTO {
    @JsonProperty(required = true, value = "event_id")
    private String sensingEventId = null;

    @JsonProperty(required = true, value = "name")
    private String name;

    @JsonProperty(value = "place_ident")
    private String placeIdent;

    @JsonAlias({"timestamp"})
    @JsonProperty(required = true, value="created")
    private long timestamp;

    @JsonProperty(required = true, value = "longitude")
    private double longitude;

    @JsonProperty(required = true, value = "latitude")
    private double latitude;

    @JsonProperty(required = false, value="tags")
    private List<SimpleTagDTO> tags;

    @JsonProperty(required = false, value="updated")
    private long updated;
}
