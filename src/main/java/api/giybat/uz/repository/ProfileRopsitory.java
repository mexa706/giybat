package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.mapper.ProfileDetailMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ProfileRopsitory extends CrudRepository<ProfileEntity, Integer>, PagingAndSortingRepository<ProfileEntity, Integer> {

    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);

    @Query(value = """
              select p.id as id , p.name as name, p.username as username, p.photo_id as photoId,p.status as status,p.created_date as createdDate,
                            (select count(*) from post as pt where pt.profile_id= p.id) as postCount ,
                     (select string_agg(pr.roles,',') from profile_role as pr where pr.profile_id= p.id) as roles
              from profile as p
            where p.visible = true
              order by p.created_date 
            """, nativeQuery = true,
            countQuery = "select count(*) from profile where visible=true")
    Page<ProfileDetailMapper> filter(PageRequest pageRequest);

    @Query(value = """
              select p.id as id , p.name as name, p.username as username, p.photo_id as photoId,p.status as status,p.created_date as createdDate,
                            (select count(*) from post as pt where pt.profile_id= p.id) as postCount ,
                     (select string_agg(pr.roles,',') from profile_role as pr where pr.profile_id= p.id) as roles
              from profile as p
            where (lower(p.username) like lower(concat('%', ?1, '%'))
                                      or lower(p.name) like lower(concat('%', ?1, '%')) ) and p.visible = true
              order by p.created_date
            """, nativeQuery = true,
            countQuery = "select count(*) from profile where (lower(p.username) like lower(concat('%', ?1, '%'))" +
                    "                                      or lower(p.name) like lower(concat('%', ?1, '%')) ) and p.visible = true")
    Page<ProfileDetailMapper> filter(String query, PageRequest pageable);


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

    @Modifying
    @Transactional
    @Query("update ProfileEntity set photoId=?1 where id=?2")
    void updatePhoto(String photoId, Integer id);

    @Query("SELECT u.password FROM ProfileEntity u WHERE u.id = :id")
    String findPasswordById(Integer id);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set tempUsername=?2 where id=?1")
    void updateTempUsername(Integer profileId, String tempUsername);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET username=?2 , tempUsername='' WHERE id=?1")
    void updateUsername(Integer profileId, String newUsername);

    @Query("select p.username , p.status ,p.createdDate, p.photo  from ProfileEntity  p where id!=?1 order by p.createdDate desc")
    List<ProfileEntity> getAll(Integer id);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set visible=false where id=?1")
    void delete(Integer profileId);
}
