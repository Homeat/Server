package homeat.backend.domain.user.repository;

import homeat.backend.domain.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = "select address_id, full_nm, emd_nm from address order by ST_DISTANCE(POINT(?1, ?2), point) asc limit ?3 offset ?4", nativeQuery = true)
    List<Object []> findOrderByPoint(Double x, Double y, int limit, int offset);

    @Query(value = "select address_id, full_nm, emd_nm from address where address_id = ?1 limit 1", nativeQuery = true)
    List<Object []> findByIdCustom(Long address_id);
}
