package spring.dacn.mercury.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.dacn.mercury.entities.ReservationTable;
import spring.dacn.mercury.repositories.ReservationTableRepository;

import java.util.List;

@Service
public class ReservationTableService {
    private final ReservationTableRepository reservationTableRepository;

    @Autowired
    public ReservationTableService(ReservationTableRepository reservationTableRepository) {
        this.reservationTableRepository = reservationTableRepository;
    }

    public List<ReservationTable> getAllReservationTables() {
        return reservationTableRepository.findAll();
    }

    public ReservationTable getReservationTableById(Long id) {
        return reservationTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Reservation Table not found"));
    }

    public ReservationTable createReservationTable(ReservationTable reservationTable) {
        return reservationTableRepository.save(reservationTable);
    }

    public ReservationTable updateReservationTable(Long id, ReservationTable reservationTable) {
        ReservationTable existingReservationTable = getReservationTableById(id);
        // Update fields here
        return reservationTableRepository.save(existingReservationTable);
    }

    public void deleteReservationTable(Long id) {
        reservationTableRepository.deleteById(id);
    }
}

