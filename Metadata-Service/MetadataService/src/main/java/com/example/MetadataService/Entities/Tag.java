package com.example.MetadataService.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tag {
    private String tagName;
    private String imageFileHash;
    private String imageObjectHash;
}
