package homeat.backend.domain.user.annotation;

import homeat.backend.domain.user.validation.ExistNicknameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistNicknameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistNickname {
    String message() default "이미 존재하는 닉네임입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
