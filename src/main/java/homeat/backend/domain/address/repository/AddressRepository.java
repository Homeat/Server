package homeat.backend.domain.address.repository;

import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.address.repository.querydsl.AddressRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, AddressRepositoryCustom {
}
