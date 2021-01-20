package com.example.MetadataService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageMetaPlusItemDTO {

    @JsonProperty(value = "events")
    private List<SimpleEventDTO> events;

    @JsonProperty(value = "current_page")
    private long currentPage;

    @JsonProperty(value = "total_items")
    private long totalItems;

    @JsonProperty(value = "total_pages")
    private long totalPages;
}
