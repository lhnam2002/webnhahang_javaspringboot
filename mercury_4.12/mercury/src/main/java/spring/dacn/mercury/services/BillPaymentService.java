package spring.dacn.mercury.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.entities.Payment;
import spring.dacn.mercury.repositories.BillPaymentRepositoty;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})

public class BillPaymentService {
    private final BillPaymentRepositoty billPaymentRepositoty;

    public List<Payment> getAllHoaDons(int pageNo, int pageSize, String sortBy)
    {
        return billPaymentRepositoty.findAllHoaHons(pageNo, pageSize, sortBy);
    }

    public List<Payment> searchHoaDons(String keyword) {
        return billPaymentRepositoty.searchHoaDons(keyword);
    }
}
