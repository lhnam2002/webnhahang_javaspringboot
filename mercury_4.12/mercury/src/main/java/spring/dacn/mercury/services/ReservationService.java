package spring.dacn.mercury.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.dacn.mercury.constants.ReservationStatus;
import spring.dacn.mercury.entities.*;
import spring.dacn.mercury.repositories.*;
import spring.dacn.mercury.utils.MailTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DiningTableRepository diningTableRepository;
    private final MenuSetRepository menuSetRepository;
    private final ReservationTableRepository reservationTableRepository;
    private final ReservationItemRepository reservationItemRepository;
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final EmailService emailService;

    // Thêm bàn vào đặt bàn
    // Trong phương thức addDiningTableToReservation
    public void addDiningTableToReservation(Reservation reservation, DiningTable diningTable) {
        if (reservation == null) {
            throw new RuntimeException("Reservation is null");
        }

        ReservationTable reservationTable = new ReservationTable();
        reservationTable.setReservation(reservation);
        reservationTable.setDiningTable(diningTable);
//        reservationTable.setArrivalTime("2024-12-31 18:00");

        reservation.addReservationTable(reservationTable);  // Thêm vào danh sách
        reservationTableRepository.save(reservationTable);  // Lưu vào cơ sở dữ liệu
    }

    // Trong phương thức addMenuSetToReservation
    public void addMenuSetToReservation(Reservation reservation, MenuSet menuSet, Integer price) {
        if (reservation == null) {
            throw new RuntimeException("Reservation is null");
        }
        if (reservation.getReservationItems() == null) {
            reservation.setReservationItems(new ArrayList<>());
        }

        ReservationItem reservationItem = new ReservationItem();
        reservationItem.setReservation(reservation);
        reservationItem.setMenuSet(menuSet);
        reservationItem.setQuantity(1);  // Giả sử số lượng mặc định là 1
        reservationItem.setPrice(price);

        reservation.addReservationItem(reservationItem);  // Thêm vào danh sách
        reservationItemRepository.save(reservationItem);  // Lưu vào cơ sở dữ liệu
    }

    // Tạo hóa đơn cho đặt bàn
    public Bill generateBill(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Tính tổng tiền của tất cả món ăn trong đặt bàn
        Integer subtotal = reservation.getReservationItems().stream()
                .map(ReservationItem::getPrice)
                .reduce(0, Integer::sum);

        // Tính giảm giá (ví dụ 10%)
        Integer discount = (int) (subtotal * 0.1); // Giảm giá 10%

        // Tính thuế (ví dụ 5%)
        Integer tax = (int) (subtotal * 0.05); // Thuế 5%

        // Tính tổng tiền sau khi giảm giá và cộng thuế
        Integer total = subtotal - discount + tax;

        // Tạo đối tượng Bill
        Bill bill = Bill.builder()
                .reservation(reservation)
                .subtotal(subtotal)
                .discount(discount)
                .tax(tax)
                .total(total)
                .build();

        return billRepository.save(bill);
    }

    // Tạo và lưu thanh toán cho hóa đơn
    public Payment createPayment(Long billId, BigDecimal amount, String paymentMethod, String paymentStatus) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        Payment payment = Payment.builder()
                .bill(bill)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .build();

        // Lưu thanh toán vào cơ sở dữ liệu
        return paymentRepository.save(payment);
    }

    // Tạo đặt bàn mới
    public Reservation createReservation(LocalDateTime reservationTime, int numberOfPeople, String specialRequest,
                                         boolean preOrderFood, String status, User user) {
        Reservation reservation = Reservation.builder()
                .reservationTime(reservationTime)
                .numberOfPeople(numberOfPeople)
                .specialRequest(specialRequest)
                .preOrderFood(preOrderFood)
//                .status(status)
                .user(user)  // Gán user vào Reservation
                .build();

        // Kiểm tra đối tượng reservation trước khi lưu vào cơ sở dữ liệu
        if (reservation == null) {
            throw new RuntimeException("Reservation object is null");
        }

        // Lưu vào cơ sở dữ liệu
        return reservationRepository.save(reservation);
    }

    // Lấy danh sách tất cả các đặt bàn
//    public List<Reservation> getAllReservations() {
//        return reservationRepository.findAll();  // Lấy tất cả đặt bàn từ cơ sở dữ liệu
//    }

    // Lấy đặt bàn theo ID
    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public Reservation createReservationV1(int numberOfPeople, LocalDateTime startTime,
                                           String specialRequest, List<Map<String, Object>> menuSets,
                                           List<Map<String, Object>> tables) {
        Reservation reservation = new Reservation();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        reservation.setUser(user);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setSpecialRequest(specialRequest);
        reservation.setPreOrderFood(true);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setLateFee(0);

        long totalPrice = 0;


        for(Map<String, Object> m : menuSets){
            ReservationItem reservationItem = new ReservationItem();
            reservationItem.setReview(null);
            reservationItem.setQuantity((int) m.get("quantity"));
            reservationItem.setPrice((Integer) m.get("price"));
            totalPrice += (long) reservationItem.getQuantity() * reservationItem.getPrice();
            MenuSet menuSet = menuSetRepository.findById(((Integer) m.get("id")).longValue())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy menuSet"));
            reservationItem.setMenuSet(menuSet);
            reservation.addReservationItem(reservationItem);
        }
        for(Map<String, Object> t : tables){
            ReservationTable reservationTable = new ReservationTable();
            DiningTable diningTable = diningTableRepository.findById(((Integer) t.get("id")).longValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Table"));
            reservationTable.setDiningTable(diningTable);
            reservationTable.setStartTime(startTime);
            reservationTable.setEndTime(startTime.plusHours(2));
            reservation.addReservationTable(reservationTable);
        }
        reservation.setTotalPrice(totalPrice);


        reservationRepository.save(reservation);
        MailTemplate mailTemplate = new MailTemplate(reservation);
        emailService.sendEmail(user.getEmail(), "Đặt bàn thành công", mailTemplate.getMail());

        return reservation;

    }

    public Page<Reservation> getAllReservationsByUser(int pageNo, int pageSize){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return reservationRepository.findAllByUser(user, pageable);
    }

    public Page<Reservation> getAllReservations(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return reservationRepository.findAll(pageable);
    }

    @Transactional
    public void updateStatus(long id, String status, boolean fee) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Reservation"));
        if(status.equals("CHECKED_IN") ){
            if(fee){
                reservation.setLateFee(100000);
            }
            reservation.setCheckInTime(LocalDateTime.now());
        }
        else if(status.equals("CANCELLED")){
            reservation.setTotalPrice(reservation.getTotalPrice() * 10 / 100);
        }
        else if(status.equals("CHECKED_OUT")){
            reservation.setCheckOutTime(LocalDateTime.now());
        }
        reservation.setStatus(ReservationStatus.valueOf(status));
        reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservationId(int pageNo, int pageSize, String sortBy)
    {
        return reservationRepository.findAllReservationId(pageNo, pageSize, sortBy);
    }

    public List<Reservation> searchReservations(String keyword) {
        return reservationRepository.searchReservations(keyword);
    }
}
