package spring.dacn.mercury.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "tables")
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number")
    private int tableNumber;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "location_description", length = 255)
    private String locationDescription;
    @Lob
    @Column(name = "image_data", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Transient
    private MultipartFile imageFile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiningTable diningTable = (DiningTable) o;  // Cập nhật tên đối tượng
        return Objects.equals(id, diningTable.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


