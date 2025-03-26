package spring.dacn.mercury.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.dacn.mercury.entities.ReservationTable;
import spring.dacn.mercury.services.ReservationTableService;

import java.util.List;

@RestController
@RequestMapping("/api/reservationtables")
public class ReservationTableController {
    private final ReservationTableService reservationTableService;

    @Autowired
    public ReservationTableController(ReservationTableService reservationTableService) {
        this.reservationTableService = reservationTableService;
    }

    @GetMapping
    public List<ReservationTable> getAllReservationTables() {
        return reservationTableService.getAllReservationTables();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationTable> getReservationTableById(@PathVariable Long id) {
        ReservationTable reservationTable = reservationTableService.getReservationTableById(id);
        return ResponseEntity.ok(reservationTable);
    }

    @PostMapping
    public ResponseEntity<ReservationTable> createReservationTable(@RequestBody ReservationTable reservationTable) {
        ReservationTable createdReservationTable = reservationTableService.createReservationTable(reservationTable);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservationTable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationTable> updateReservationTable(@PathVariable Long id, @RequestBody ReservationTable reservationTable) {
        ReservationTable updatedReservationTable = reservationTableService.updateReservationTable(id, reservationTable);
        return ResponseEntity.ok(updatedReservationTable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTable(@PathVariable Long id) {
        reservationTableService.deleteReservationTable(id);
        return ResponseEntity.noContent().build();
    }


}
