package api.giybat.uz.repository;

import api.giybat.uz.entity.EmailHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailHistoryRepository extends CrudRepository<EmailHistoryEntity, String> {
    // select count(*) from sms_history where phone=? and created_date between ? and ?
    Long countByEmailAndCreatedDateBetween(String email, LocalDateTime from, LocalDateTime to);

    Optional<EmailHistoryEntity> findTop1ByEmailOrderByCreatedDateDesc(String email);


    @Modifying
    @Transactional
    @Query("update EmailHistoryEntity set attemptCount=coalesce(attemptCount, 0)+1 where id=?1")
    void updateAttemptCount(String id);
}
