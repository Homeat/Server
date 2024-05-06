package homeat.backend.test;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.address.repository.AddressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @DisplayName("첫번째 동네 조회")
    @Test
    public void testFindById1() {
        Address add = addressRepository.findById(1l).orElseThrow();

        System.out.println(add.getFullNm());
    }

    @DisplayName("현재 가장 가까운 동네 조회")
    @Test
    public void testFindFirstByPointDistance() {
        AddressResponse.AddressDTO add = addressRepository.findFirstByPointDistance(126.9221, 37.5617);

        System.out.println(add.getFullNm());
    }
}
