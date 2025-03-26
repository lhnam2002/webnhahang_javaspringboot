package spring.dacn.mercury.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spring.dacn.mercury.entities.DiningTable;
import spring.dacn.mercury.entities.ReservationTable;
import spring.dacn.mercury.repositories.DiningTableRepository;
import spring.dacn.mercury.repositories.ReservationTableRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiningTableService {
    private final DiningTableRepository diningTableRepository;
    private final ReservationTableRepository reservationTableRepository;

    @Autowired
    public DiningTableService(DiningTableRepository diningTableRepository,
                              ReservationTableRepository reservationTableRepository) {
        this.diningTableRepository = diningTableRepository;
        this.reservationTableRepository = reservationTableRepository;
    }

    public List<DiningTable> getAllDiningTables(int pageNo, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<DiningTable> pagedResult = diningTableRepository.findAll(pageable);
        return pagedResult.getContent();
    }

    public DiningTable getDiningTableById(Long id) {
        return diningTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DiningTable not found"));
    }

    public DiningTable createDiningTable(
            DiningTable diningTable) {
        return diningTableRepository.save(diningTable);
    }

    public DiningTable updateDiningTable(Long id, DiningTable diningTable) {
        DiningTable existingTable = getDiningTableById(id);
        // Cập nhật các trường từ đối tượng diningTable được truyền vào
        existingTable.setTableNumber(diningTable.getTableNumber());
        existingTable.setCapacity(diningTable.getCapacity());
        existingTable.setLocationDescription(diningTable.getLocationDescription());
        return diningTableRepository.save(existingTable);
    }


    public void deleteDiningTable(Long id) {
        diningTableRepository.deleteById(id);
    }

    public int getTotalPages(int pageSize) {
        long totalRecords = diningTableRepository.count(); // Lấy tổng số bản ghi
        return (int) Math.ceil((double) totalRecords / pageSize); // Tính tổng số trang
    }
    

    public int getTotalDiningTables() {
        // Sử dụng phương thức count() để đếm tổng số bản ghi trong bảng DiningTable
        return (int) diningTableRepository.count();
    }

    public boolean deleteDiningTableById(Long id) {
        if (diningTableRepository.existsById(id)) {
            diningTableRepository.deleteById(id);
            return true; // Xóa thành công
        }
        return false; // Không tìm thấy bàn với ID này
    } public void addDiningTable(DiningTable diningTable) {
        diningTableRepository.save(diningTable); // Lưu đối tượng DiningTable vào cơ sở dữ liệu
    }

    public List<DiningTable> getAllDiningTables() {
        return diningTableRepository.findAll(); // Trả về danh sách tất cả các bàn ăn
    }

    public DiningTable findById(Long id) {
        Optional<DiningTable> diningTable = diningTableRepository.findById(id);
        return diningTable.orElseThrow(() -> new RuntimeException("Dining table not found with id: " + id));
    }

    public void save(DiningTable existingDiningTable) {
        diningTableRepository.save(existingDiningTable);
    }

    public List<DiningTable> getAllTableByTime(LocalDateTime startTime){
        List<DiningTable> reservationTables =
                reservationTableRepository.findConflictingTables(startTime, startTime.plusHours(2));

        List<DiningTable> allTables = diningTableRepository.findAll();

        allTables.removeAll(reservationTables);

        return allTables;
    }
}

