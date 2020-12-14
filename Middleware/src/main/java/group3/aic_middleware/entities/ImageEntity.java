package group3.aic_middleware.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {

    @Getter
    @Setter
    private String base64EncodedImage;

}
