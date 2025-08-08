package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfileRopsitory extends CrudRepository<ProfileEntity, Integer> {

    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set status=?2 where id=?1")
    void changeStatus(Integer profileId, GeneralStatus status);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set password=?2 where id=?1")
    void updatePassword(Integer profileId, String password);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set name=?2 where id=?1")
    void updateDetail(Integer profileId, String name);


    @Query("SELECT u.password FROM ProfileEntity u WHERE u.id = :id")
    String findPasswordById(Integer id);


}
