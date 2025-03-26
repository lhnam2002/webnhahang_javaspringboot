package spring.dacn.mercury.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring.dacn.mercury.entities.Reservation;
import spring.dacn.mercury.entities.Review;
import spring.dacn.mercury.services.ReviewService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewControllerv1 {
    private final ReviewService reviewService;

    @GetMapping("")
    public String getAllReservations(Model model,
                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                     @RequestParam(defaultValue = "8") Integer pageSize){

        Page<Review> reviewPage = reviewService.getAllReview(pageNo, pageSize);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        return "admin-reviews/list";
    }
}
