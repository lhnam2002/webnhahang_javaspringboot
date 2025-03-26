package spring.dacn.mercury.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.entities.Reservation;
import spring.dacn.mercury.repositories.MenuSetRepository;
import spring.dacn.mercury.repositories.ReviewRepository;
import spring.dacn.mercury.services.MenuSetService;
import spring.dacn.mercury.repositories.ReviewRepository;
import spring.dacn.mercury.dto.response.MenuSetWithReview;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuSetService menuSetService;
    private final ReviewRepository reviewRepository;

    @GetMapping
    public String showAllMenuSets(@NotNull Model model,
                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                  @RequestParam(defaultValue = "6") Integer pageSize,
                                  @RequestParam(defaultValue = "id") String sortBy) {

        // Lấy danh sách sản phẩm và tổng số sản phẩm từ service
        List<MenuSet> menuSets = menuSetService.getAllMenusets(pageNo - 1, pageSize, sortBy);
        int totalItems = menuSetService.getTotalMenuSets(); // Giả sử có phương thức này để lấy tổng số sản phẩm

        // Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        List<MenuSetWithReview> menuSetWithReviews = new ArrayList<>();

        for (MenuSet menuSet : menuSets) {
            Double averageRating = reviewRepository.findAverageRatingByMenuSetId(menuSet.getId());
            MenuSetWithReview menuSetWithReview = MenuSetWithReview.builder()
                    .menuSet(menuSet)
                    .countReview(reviewRepository.countAllByMenuSet(menuSet))
                    .rating(averageRating == null ? 0 : Math.round(averageRating))
                    .isBestSeller(false)
                    .build();
            menuSetWithReviews.add(menuSetWithReview);
        }

        long maxRating = menuSetWithReviews.stream()
                .mapToLong(MenuSetWithReview::getRating)
                .max()
                .orElse(0);

        if(maxRating >= 4 ){
            menuSetWithReviews.stream()
                    .filter(menu -> menu.getRating() == maxRating)
                    .forEach(menu -> menu.setBestSeller(true));
        }

        menuSetWithReviews.sort((a, b) -> Long.compare(b.getRating(), a.getRating()));


        model.addAttribute("menuSetWithReviews", menuSetWithReviews);
        model.addAttribute("reservation", new Reservation());

        // Gán các thuộc tính cho model
        model.addAttribute("menusets", menuSets);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", totalPages);

        return "menu/menulist";
    }

}