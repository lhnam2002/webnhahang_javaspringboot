package spring.dacn.mercury.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "menu_sets")
public class MenuSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    public String getFormattedPrice() {
        if (price == null) {
            return "0 VND";
        }
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " VND";
    }

    @Lob
    @Column(name = "image_data", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Transient
    private MultipartFile imageFile;
}
