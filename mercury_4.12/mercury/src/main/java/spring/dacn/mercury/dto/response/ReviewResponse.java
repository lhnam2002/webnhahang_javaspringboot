package spring.dacn.mercury.dto.response;

import lombok.*;
import spring.dacn.mercury.entities.Review;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private long id;
    private long userId;
    private long menuSetId;
    private int rating;
    private String content;

    public static ReviewResponse fromReview(Review review){
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .menuSetId(review.getMenuSet().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .build();
    }
}
