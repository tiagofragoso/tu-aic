package com.example.MetadataService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTagDTO {
    @JsonProperty(required = true, value = "tag_name")
    private String tagName;
}