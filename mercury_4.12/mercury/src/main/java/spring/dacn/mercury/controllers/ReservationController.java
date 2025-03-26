package spring.dacn.mercury.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import spring.dacn.mercury.dto.response.MenuSetWithReview;
import spring.dacn.mercury.entities.*;
import spring.dacn.mercury.repositories.ReservationRepository;
import spring.dacn.mercury.repositories.ReviewRepository;
import spring.dacn.mercury.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final DiningTableService diningTableService;  // Dịch vụ lấy bàn
    private final MenuSetService menuSetService;          // Dịch vụ lấy món ăn
    private final ReservationService reservationService;  // Dịch vụ xử lý đặt bàn
    private final UserService userService;
    private final ReviewRepository reviewRepository;

    // Hiển thị form tạo đặt bàn
    @GetMapping("/create")
    public String showReservationForm(Model model) {
        List<DiningTable> diningTables = diningTableService.getAllDiningTables();
        List<MenuSet> menuSets = menuSetService.getAllMenusets();
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

        model.addAttribute("diningTables", diningTables);
        model.addAttribute("menuSetWithReviews", menuSetWithReviews);
        model.addAttribute("reservation", new Reservation());

        return "reservation/reservation_form";
    }


    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReservation(@RequestBody Map<String, Object> request){
        int numberOfPeople = (int)request.get("numberOfPeople");
        String reservationTimeStr = (String) request.get("reservationTime");
        LocalDateTime reservationTime = LocalDateTime.parse(reservationTimeStr);
        String specialRequest = (String) request.get("specialRequest");

        List<Map<String, Object>> menuSets = (List<Map<String, Object>>) request.get("menuSets");
        List<Map<String, Object>> tables = (List<Map<String, Object>>) request.get("tables");
        Reservation reservation = reservationService.createReservationV1(numberOfPeople, reservationTime,
                specialRequest,
                menuSets, tables);
        return ResponseEntity.ok().body(reservation.getId());
    }

    @GetMapping("/create/success")
    public String viewSuccess(@RequestParam Long id, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Reservation reservation = reservationService.getReservationById(id);
        int totalPrice = reservation.getReservationItems().stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("reservation", reservation);
        model.addAttribute("message", "Đặt bàn thành công!");
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("user", user);
        return "reservation/reservation_success";
    }

    // Xem chi tiết đặt bàn
    @GetMapping("/{reservationId}")
    public String viewReservationDetails(@PathVariable("reservationId") Long reservationId, Model model) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        model.addAttribute("reservation", reservation);
        return "reservation/reservation_details";  // View hiển thị chi tiết đặt bàn
    }

    // Tạo hóa đơn cho đặt bàn
    @PostMapping("/{reservationId}/generateBill")
    public String generateBill(@PathVariable("reservationId") Long reservationId, Model model) {
        Bill bill = reservationService.generateBill(reservationId);
        model.addAttribute("bill", bill);
        return "reservation/bill_details";  // View hiển thị hóa đơn
    }

    // Thanh toán hóa đơn
    @PostMapping("/{reservationId}/payBill")
    public String payBill(@PathVariable("reservationId") Long reservationId,
                          @RequestParam("amount") BigDecimal amount,
                          @RequestParam("paymentMethod") String paymentMethod,
                          @RequestParam("paymentStatus") String paymentStatus,
                          Model model) {
        Bill bill = reservationService.generateBill(reservationId);
        reservationService.createPayment(bill.getId(), amount, paymentMethod, paymentStatus);

        model.addAttribute("message", "Thanh toán thành công!");
        return "reservation/payment_success";  // View thanh toán thành công
    }

    @GetMapping("")
    public String getAllReservations(Model model,
                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                     @RequestParam(defaultValue = "8") Integer pageSize){
        Page<Reservation> reservationPage = reservationService.getAllReservations(pageNo, pageSize);
        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", reservationPage.getTotalPages());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "admin-reservations/list";
    }


    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("status") String status,
                               @RequestParam(required = false, defaultValue = "false") boolean fee,
                               @RequestParam("reservationId") long id){
        reservationService.updateStatus(id, status, fee);
        return "redirect:/reservations";
    }

    @GetMapping("/detail/{id}")
    public String showReservationDetail(@NotNull Model model, @PathVariable long id) {
        Reservation reservation = reservationService.getReservationById(id);
        model.addAttribute("reservation", reservation);
        return "admin-reservations/detail";
    }

    @GetMapping("/search")
    public String searchIdResvation(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "15") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("reservations", reservationService.searchReservations(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                reservationService.getAllReservationId(pageNo , pageSize, sortBy).size() / pageSize);
        return "admin-reservations/list";
    }
}
