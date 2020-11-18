package com.example.MetadataService.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class Tag {

    @Indexed(unique = true)
    private String tagName;

    private String imageHash;
}
