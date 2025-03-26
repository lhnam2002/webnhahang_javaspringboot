package spring.dacn.mercury.dto.response;

import lombok.*;
import spring.dacn.mercury.entities.MenuSet;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuSetWithReview {
    private MenuSet menuSet;
    private long rating;
    private int countReview;
    private boolean isBestSeller;
}
