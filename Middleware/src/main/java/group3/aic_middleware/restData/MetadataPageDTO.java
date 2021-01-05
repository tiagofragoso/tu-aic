package group3.aic_middleware.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MetadataPageDTO {

    @JsonProperty(value = "events")
    private List<ReadEventsDTO> events;

    @JsonProperty(value = "current_page")
    private long currentPage;

    @JsonProperty(value = "total_items")
    private long totalItems;

    @JsonProperty(value = "total_pages")
    private long totalPages;
}
