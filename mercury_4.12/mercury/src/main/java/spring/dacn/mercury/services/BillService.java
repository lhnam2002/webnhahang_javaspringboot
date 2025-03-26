package spring.dacn.mercury.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.dacn.mercury.entities.Bill;
import spring.dacn.mercury.repositories.BillRepository;

import java.util.List;

@Service
public class BillService {
    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bill not found"));
    }

    public Bill createBill(Bill bill) {
        return billRepository.save(bill);
    }

    public Bill updateBill(Long id, Bill bill) {
        Bill existingBill = getBillById(id);
        // Update fields here
        return billRepository.save(existingBill);
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }
}
