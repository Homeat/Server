package homeat.backend.domain.user.validation;

import homeat.backend.domain.user.annotation.ExistEmail;
import homeat.backend.domain.user.annotation.ExistNickname;
import homeat.backend.domain.user.handler.MemberErrorStatus;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class ExistNicknameValidator implements ConstraintValidator<ExistNickname, String> {

    private final MemberRepository memberRepository;
    @Override
    public void initialize(ExistNickname constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = memberRepository.findByNickname(value).isPresent();

        if (isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MemberErrorStatus.EXIST_NICKNAME.toString())
                    .addConstraintViolation();
        }

        return !isValid;
    }
}