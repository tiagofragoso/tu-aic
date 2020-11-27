package group3.aic_middleware.restData;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageObjectServiceLoadDTO {

    @Getter
    @Setter
    private String base64Image;

    @Getter
    @Setter
    private LocalDateTime lastModified;

}
