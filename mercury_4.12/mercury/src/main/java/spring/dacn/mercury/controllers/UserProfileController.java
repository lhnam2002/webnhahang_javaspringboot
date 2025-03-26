package spring.dacn.mercury.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.dacn.mercury.entities.User;
import spring.dacn.mercury.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewProfile(Model model) {
        User user = userService.getCurrentUser();  // Lấy người dùng hiện tại
        model.addAttribute("user", user);
        return "user/profile";  // Trang chỉ xem thông tin
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {
        User user = userService.getCurrentUser();  // Lấy người dùng hiện tại
        model.addAttribute("user", user);
        return "user/edit-profile";  // Trang chỉnh sửa thông tin
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) MultipartFile avatar,
                                Model model) {
        try {
            String avatarUrl = null;
            if (avatar != null && !avatar.isEmpty()) {
                // Lưu ảnh lên thư mục và lấy URL
                avatarUrl = userService.saveAvatar(avatar);  // Phương thức để lưu ảnh và trả về URL
            }

            // Cập nhật thông tin người dùng (bao gồm avatar nếu có)
            userService.updateUserProfile(username, email, phone, avatar);

            model.addAttribute("message", "Profile updated successfully.");
            return "redirect:/profile";  // Redirect về trang profile sau khi cập nhật
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "user/edit-profile";  // Quay lại trang chỉnh sửa nếu có lỗi
        }
    }
}


