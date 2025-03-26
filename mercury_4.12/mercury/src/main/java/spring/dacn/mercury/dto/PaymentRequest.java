package spring.dacn.mercury.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PaymentRequest {
    private BigDecimal amount;
    private String paymentMethod;
}