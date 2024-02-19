package homeat.backend.domain.user.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberinfo_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birth;

    private String accountType;

    private String accountNumber;

    private String phoneNumber;

    private Long address;

    private Long income;

    public void updateAddress(Long address) {
        this.address = address;
    }

    public void updateIncome(Long income) {
        this.income = income;
    }
}