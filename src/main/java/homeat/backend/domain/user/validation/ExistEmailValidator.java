package homeat.backend.domain.user.validation;

import homeat.backend.domain.user.annotation.ExistEmail;
import homeat.backend.domain.user.handler.MemberErrorStatus;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

@Component
@RequiredArgsConstructor
public class ExistEmailValidator implements ConstraintValidator<ExistEmail, String> {

    private final MemberRepository memberRepository;
    @Override
    public void initialize(ExistEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = memberRepository.findByEmail(value).isPresent();

        if (isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorStatus.EXIST_EMAIL.toString())
                    .addConstraintViolation();
        }

        return !isValid;
    }
}
