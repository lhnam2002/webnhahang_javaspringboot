package spring.dacn.mercury.controllers;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.services.MenuSetService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/menusets")
@RequiredArgsConstructor
public class MenuSetController {
    private final MenuSetService menuSetService;

    @GetMapping
    public String showAllMenuSets(@NotNull Model model,
                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                  @RequestParam(defaultValue = "4") Integer pageSize,
                                  @RequestParam(defaultValue = "id") String sortBy) {

        // Lấy danh sách sản phẩm và tổng số sản phẩm từ service
        List<MenuSet> menuSets = menuSetService.getAllMenusets(pageNo -1, pageSize, sortBy);
        int totalItems = menuSetService.getTotalMenuSets(); // Giả sử có phương thức này để lấy tổng số sản phẩm

        // Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Gán các thuộc tính cho model
        model.addAttribute("menusets", menuSets);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", totalPages);

        return "menuset/list";
    }

    @GetMapping("/add")
    public String addMenusetForm(@NotNull Model model) {
        model.addAttribute("menuset", new MenuSet());
        return "menuset/add";
    }

    @PostMapping("/add")
    public String addBook(
            @Valid @ModelAttribute("menuset") MenuSet menuSet,
            @RequestPart("imageFile") MultipartFile imageFile,
            @org.antlr.v4.runtime.misc.NotNull BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);

            return "menuset/add";
        }

        if (!imageFile.isEmpty()) {
            try {
                // Lưu dữ liệu ảnh vào thuộc tính imageData của đối tượng book
                menuSet.setImageData(imageFile.getBytes());
            } catch (IOException e) {
                // Xử lý ngoại lệ nếu có lỗi khi đọc dữ liệu ảnh
                e.printStackTrace();
                // Thêm thông báo lỗi vào model
                model.addAttribute("error", "Failed to upload image.");

                return "menuset/add";
            }
        }

        // Lưu thông tin sản phẩm vào cơ sở dữ liệu
        menuSetService.addMenuSet(menuSet);
        return "redirect:/menusets";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] getBookImage(@PathVariable("id") Long id) {
        return menuSetService.getMenuSetById(id)
                .map(MenuSet::getImageData)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
    }

    @GetMapping("/edit/{id}")
    public String editMenuSetForm(@NotNull Model model, @PathVariable long id) {
        var menuSet = menuSetService.getMenuSetById(id);
        model.addAttribute("menuset", menuSet.orElseThrow(() -> new IllegalArgumentException("Menu not found")));
        return "menuset/edit";
    }

    @PostMapping("/edit")
    public String editBook(@Valid @ModelAttribute("menuset") MenuSet menuSet,
                           @RequestPart("imageFile") MultipartFile imageFile,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "menuset/edit";
        }

        if (!imageFile.isEmpty()) {
            try {
                menuSet.setImageData(imageFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Failed to upload image.");

                return "menuset/edit";
            }
        }

        menuSetService.updateMenu(menuSet);
        return "redirect:/menusets";
    }


    @GetMapping("/delete/{id}")
    public String deleteMenuset(@PathVariable long id) {
        menuSetService.getMenuSetById(id)
                .ifPresentOrElse(
                        menuSet -> menuSetService.deleteMenuSetById(id),
                        () -> {
                            throw new IllegalArgumentException("Menu not found");
                        });
        return "redirect:/menusets";
    }

    @GetMapping("/search")
    public String searchMenu(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("menusets", menuSetService.searchMenu(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                menuSetService.getAllMenusets(pageNo , pageSize, sortBy).size() / pageSize);
        return "menuset/list";
    }
}
