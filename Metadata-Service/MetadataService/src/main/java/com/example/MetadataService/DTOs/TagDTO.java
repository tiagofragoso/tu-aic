package com.example.MetadataService.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagDTO {
    private String sensingEventId;
    private String tagName;
    private String imageHash;

    /**
     * Constructor to return a tag.
     * @param tagName The tag name.
     * @param imageHash The image hash.
     */
    public TagDTO(String tagName, String imageHash) {
        this.tagName = tagName;
        this.imageHash = imageHash;
    }
}
