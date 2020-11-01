package com.example.MetadataService.Entities;

public class BaseTag extends Tag {
    public BaseTag(String imageFileHash, String imageObjectHash) {
        super("Base", imageFileHash, imageObjectHash);
    }
}
