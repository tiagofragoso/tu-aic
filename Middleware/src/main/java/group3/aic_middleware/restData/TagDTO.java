package group3.aic_middleware.restData;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {

    @Getter
    @Setter
    private String tagName;

    @Getter
    @Setter
    private int imageHash;

}
