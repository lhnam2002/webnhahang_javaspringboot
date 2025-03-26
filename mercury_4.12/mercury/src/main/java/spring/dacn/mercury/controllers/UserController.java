package spring.dacn.mercury.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.entities.Reservation;
import spring.dacn.mercury.entities.Review;
import spring.dacn.mercury.entities.User;
import spring.dacn.mercury.services.ReservationService;
import spring.dacn.mercury.services.ReviewService;
import spring.dacn.mercury.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "user/register";
        }
        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        return "redirect:/login";
    }

    @GetMapping("/my-reservations")
    public String showAllMenuSets(@NotNull Model model,
                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                  @RequestParam(defaultValue = "8") Integer pageSize) {

        Page<Reservation> reservationPage = reservationService.getAllReservationsByUser(pageNo, pageSize);
        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        return "user-reservations/list";
    }

    @GetMapping("/my-reservations/{id}")
    public String showReservationDetail(@NotNull Model model, @PathVariable long id) {
        Reservation reservation = reservationService.getReservationById(id);
        model.addAttribute("reservation", reservation);
        return "user-reservations/detail";
    }

    @PostMapping("/create-review")
    public String createReview(@RequestParam("rating") int rating,
                               @RequestParam("reviewContent") String reviewContent,
                               @RequestParam("productId") Long productId,
                               @RequestParam("reservationItemId") Long reservationItemId,
                               @RequestParam("reservationId") Long reservationId){
        Review review = reviewService.createReviewV1(rating, reviewContent, productId, reservationItemId);
        return "redirect:/my-reservations/" + reservationId;
    }
}