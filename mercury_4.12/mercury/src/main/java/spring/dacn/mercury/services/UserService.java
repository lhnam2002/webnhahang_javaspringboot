package spring.dacn.mercury.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import spring.dacn.mercury.constants.Provider;
import spring.dacn.mercury.constants.Role;
import spring.dacn.mercury.entities.User;
import spring.dacn.mercury.repositories.RoleRepository;
import spring.dacn.mercury.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class, Throwable.class})
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder()
                .encode(user.getPassword()));
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class, Throwable.class})
    public void setDefaultRole(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public void saveOauthUser(String email, @NotNull String username) {
        if(userRepository.findByUsername(username).isPresent())
            return;
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(username));
        user.setProvider(Provider.GOOGLE.value);
        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
        userRepository.save(user);
    }
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Value("${avatar.upload.dir}")
    private String avatarUploadDir;

    public String uploadAvatar(MultipartFile profileImage) throws IOException {
        if (profileImage.isEmpty()) {
            return null;  // Không có avatar, không thay đổi
        }

        // Đặt tên file avatar mới
        String filename = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
        Path path = Paths.get(avatarUploadDir, filename);

        // Lưu avatar vào thư mục
        Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL của avatar (hoặc đường dẫn lưu trữ)
        return "/avatars/" + filename;
    }


    public void updateUserProfile(String username, String email, String phone, MultipartFile avatar) throws Exception {
        User user = getCurrentUser(); // Hoặc bạn có thể lấy user qua ID nếu cần.

        // Cập nhật thông tin user
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);

        // Nếu có avatar mới, xử lý việc lưu ảnh
        if (avatar != null && !avatar.isEmpty()) {
            String avatarPath = saveAvatar(avatar); // Lưu avatar và trả về đường dẫn ảnh.
            user.setAvatarUrl(avatarPath); // Cập nhật avatarUrl cho user.
        }

        // Lưu thông tin user vào cơ sở dữ liệu
        userRepository.save(user);
    }
    public String saveAvatar(MultipartFile avatar) throws IOException {
        // Định nghĩa thư mục lưu trữ ảnh
        String uploadDir = "src/main/resources/static/images/avatars";  // Đường dẫn lưu ảnh

        // Tạo thư mục nếu chưa tồn tại
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Lấy tên file và lưu file vào thư mục
        String fileName = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
        File file = new File(uploadDir, fileName);
        avatar.transferTo(file);  // Lưu file vào server

        // Trả về URL của ảnh để lưu vào database (hoặc để hiển thị)
        return "/images/avatars/" + fileName;
    }


    public List<GrantedAuthority> getAuthorities(String userName){
        User user = userRepository.findByUsername(userName).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

}
