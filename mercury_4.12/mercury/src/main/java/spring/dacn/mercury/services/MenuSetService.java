package spring.dacn.mercury.services;

import spring.dacn.mercury.entities.DiningTable;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.repositories.MenuSetRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})

public class MenuSetService {
    private final MenuSetRepository menuSetRepository;

    public List<MenuSet> getAllMenusets(int pageNo, int pageSize, String sortBy)
    {
        return menuSetRepository.findAllMenuSets(pageNo, pageSize, sortBy);
    }

    public Optional<MenuSet> getMenuSetById(Long id)
    {
        return menuSetRepository.findById(id);
    }

    public void addMenuSet(MenuSet menuSet)
    {
        menuSetRepository.save(menuSet);
    }

    public void updateMenu(@NotNull MenuSet menuSet) {
        MenuSet existingBook = menuSetRepository.findById(menuSet.getId()).orElse(null);
        Objects.requireNonNull(existingBook).setName(menuSet.getName());
        existingBook.setDescription(menuSet.getDescription());
        existingBook.setPrice(menuSet.getPrice());
        if (menuSet.getImageData() != null) {
            existingBook.setImageData(menuSet.getImageData());
        }
        menuSetRepository.save(existingBook);
    }
    public List<MenuSet> getMenusByReservationId(Long reservationId) {
        return menuSetRepository.findAllByReservationId(reservationId);
    }

    public String getImageBase64(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }
    public void deleteMenuSetById(Long id)
    {
        menuSetRepository.deleteById(id);
    }

    public List<MenuSet> searchMenu(String keyword) {
        return menuSetRepository.searchMenu(keyword);
    }

    public int getTotalMenuSets() {
        return (int) menuSetRepository.count(); // Đếm tổng số sản phẩm
    }

    public List<MenuSet> findAll() {
        return menuSetRepository.findAll();
    }
    public List<MenuSet> getAllMenusets() {
        return menuSetRepository.findAll(); // Trả về danh sách tất cả các bàn ăn
    }

}
