package group3.aic_middleware.restData;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageObjectServiceCreateDTO {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String base64Image;

}
