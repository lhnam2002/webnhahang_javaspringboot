package spring.dacn.mercury.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.dacn.mercury.entities.DiningTable;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.repositories.DiningTableRepository;
import spring.dacn.mercury.services.DiningTableService;
import spring.dacn.mercury.services.UserService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/tables")
public class DiningTableController {

    private final DiningTableService diningTableService;
    private UserService diningTableRepository;

    @Autowired
    public DiningTableController(DiningTableService diningTableService) {
        this.diningTableService = diningTableService;
    }

    // Hiển thị danh sách bàn với phân trang và sắp xếp
    @GetMapping
    public String showAllDiningTables(@NotNull Model model,
                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                      @RequestParam(defaultValue = "6") Integer pageSize,
                                      @RequestParam(defaultValue = "id") String sortBy) {

        // Lấy danh sách bàn và tổng số bàn từ service
        List<DiningTable> diningTables = diningTableService.getAllDiningTables(pageNo - 1, pageSize, sortBy);
        int totalItems = diningTableService.getTotalDiningTables(); // Giả sử có phương thức này để lấy tổng số bàn

        // Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Gán các thuộc tính cho model
        model.addAttribute("diningTables", diningTables);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", totalPages);

        return "table/list"; // Tên view cho danh sách bàn
    }


    // Hiển thị form thêm mới bàn
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("diningTable", new DiningTable());
        return "table/add"; // Tên view cho form thêm bàn
    }

    // Xử lý thêm mới bàn
    @PostMapping("/add")
    public String addDiningTable(@ModelAttribute("diningTable") DiningTable diningTable,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (!imageFile.isEmpty()) {
                diningTable.setImageData(imageFile.getBytes());
            }
            diningTableService.addDiningTable(diningTable);
            redirectAttributes.addFlashAttribute("message", "Dining table added successfully!");
            return "redirect:/tables";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image.");
            return "table/add";
        }
    }


    // Hiển thị form chỉnh sửa bàn
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        // Lấy thông tin DiningTable từ database
        DiningTable diningTable = diningTableService.findById(id);
        if (diningTable == null) {
            // Xử lý nếu không tìm thấy bàn
            throw new IllegalArgumentException("Invalid table ID: " + id);
        }
        model.addAttribute("diningTable", diningTable);
        return "table/edit"; // Đảm bảo file edit.html nằm trong thư mục templates/table/
    }

    // Cập nhật bàn
    @PostMapping("/edit")
    public String editDiningTable(@ModelAttribute("diningTable") DiningTable diningTable,
                                  @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra nếu không tìm thấy DiningTable trong cơ sở dữ liệu
            DiningTable existingDiningTable = diningTableService.findById(diningTable.getId());
            if (existingDiningTable == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Table not found!");
                return "redirect:/tables";
            }

            // Cập nhật các thuộc tính cơ bản từ form
            existingDiningTable.setTableNumber(diningTable.getTableNumber());
            existingDiningTable.setCapacity(diningTable.getCapacity());
            existingDiningTable.setLocationDescription(diningTable.getLocationDescription());

            // Nếu có file hình ảnh được upload, lưu vào thuộc tính imageData
            if (imageFile != null && !imageFile.isEmpty()) {
                existingDiningTable.setImageData(imageFile.getBytes());
            }

            // Cập nhật thông tin bàn trong cơ sở dữ liệu
            diningTableService.save(existingDiningTable);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Table updated successfully!");
        } catch (Exception e) {
            // Xử lý lỗi và thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating table: " + e.getMessage());
        }

        return "redirect:/tables"; // Redirect về trang danh sách bàn
    }



    // Xử lý xóa bàn
    @GetMapping("/delete/{id}")
    public String deleteDiningTable(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (diningTableService.deleteDiningTableById(id)) {
            redirectAttributes.addFlashAttribute("message", "Dining table deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Dining table not found.");
        }
        return "redirect:/tables";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getDiningTableImage(@PathVariable("id") Long id) {
        DiningTable diningTable = diningTableService.getDiningTableById(id);
        if (diningTable == null || diningTable.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(diningTable.getImageData(), headers, HttpStatus.OK);
    }
}
