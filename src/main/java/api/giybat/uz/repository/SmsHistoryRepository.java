package api.giybat.uz.repository;

import api.giybat.uz.entity.SmsHistoryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SmsHistoryRepository extends CrudRepository<SmsHistoryEntity, String> {
    // select count(*) from sms_history where phone=? and created_date between ? and ?
    Long countByPhoneAndCreatedDateBetween(String phone, LocalDateTime from, LocalDateTime to);

    Optional<SmsHistoryEntity> findTop1ByPhoneAndOrderByCreatedDateDesc(String phone);

}
