package homeat.backend.test;

import homeat.backend.domain.user.entity.LoginType;
import homeat.backend.domain.user.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testFindById1() {
//        Boolean isExist = memberRepository.existsByEmailAndLoginType("1", LoginType.KAKAO);
//        System.out.println(isExist);
    }
}
