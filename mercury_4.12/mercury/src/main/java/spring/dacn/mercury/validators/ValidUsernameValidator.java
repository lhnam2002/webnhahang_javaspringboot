package spring.dacn.mercury.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.dacn.mercury.services.UserService;
import spring.dacn.mercury.validators.annotations.ValidUsername;

@Component
@NoArgsConstructor(force = true)
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private final UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if(userService == null)
        {
            return true;
        }
        return userService.findByUsername(username).isEmpty();
    }
}