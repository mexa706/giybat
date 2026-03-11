package api.giybat.uz.repository;

import api.giybat.uz.entity.PostEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.method.P;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;


public interface PostRopsitory extends CrudRepository<PostEntity, String> , PagingAndSortingRepository<PostEntity, String> {

    Page<PostEntity> getAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(Integer profileId, PageRequest pageable);

    Optional<PostEntity> findByIdAndVisibleTrue(String id);

    @Modifying
    @Transactional
    @Query("UPDATE PostEntity p " +
            "SET p.title = ?2, " +
            "    p.content = ?3, " +
            "    p.photoId = ?4 " +
            "WHERE p.id = ?1")
    void updatePost(String id, String title, String content, String photoId);

    @Transactional
    @Modifying
    @Query("update PostEntity set visible=false where id=?1")
    void delete(String id);


    @Query("select p.profileId from PostEntity p where p.id = ?1")
    Integer findProfileIdByPostId(String id);


    @Query("select p from PostEntity p where p.id != ?1 and p.visible = true order by p.createdDate desc limit 3")
    List<PostEntity> getSimilarPostList(String exceptId);


    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.profile.id = ?1")
    Integer getPostCountByProfileId(Integer profileId);

}
