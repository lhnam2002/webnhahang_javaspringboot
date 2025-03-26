package spring.dacn.mercury.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.dacn.mercury.entities.*;
import spring.dacn.mercury.repositories.MenuSetRepository;
import spring.dacn.mercury.repositories.ReservationItemRepository;
import spring.dacn.mercury.repositories.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MenuSetRepository menuSetRepository;
    private final UserService userService;
    private final ReservationItemRepository reservationItemRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
    }

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Long id, Review review) {
        Review existingReview = getReviewById(id);
        // Update fields here
        return reviewRepository.save(existingReview);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }


    @Transactional
    public Review createReviewV1(int rating, String reviewContent, Long productId, Long reservationItemId){
        MenuSet menuSet = menuSetRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy MenuSet"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ReservationItem reservationItem = reservationItemRepository.findById(reservationItemId)
                .orElseThrow(() -> new RuntimeException("Not found ReservationItem"));

        Review review = Review.builder()
                .content(reviewContent)
                .rating(rating)
                .menuSet(menuSet)
                .user(user)
                .build();
        reviewRepository.save(review);
        reservationItem.setReview(review);
        reservationItemRepository.save(reservationItem);
        return review;
    }

    public Page<Review> getAllReview(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return reviewRepository.findAll(pageable);
    }

}
