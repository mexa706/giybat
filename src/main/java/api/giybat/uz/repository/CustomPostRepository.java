package api.giybat.uz.repository;

import api.giybat.uz.dto.FilterResultDTO;
import api.giybat.uz.dto.post.PostAdminFilterDTO;
import api.giybat.uz.dto.post.PostFilterDTO;
import api.giybat.uz.entity.PostEntity;
import api.giybat.uz.mapper.PostDetailMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class CustomPostRepository {
    @Autowired
    private EntityManager entityManager;

    public FilterResultDTO<PostEntity> filter(PostFilterDTO dto, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder(" where p.visible = true ");

        Map<String, Object> params = new HashMap<>();


        if (dto.getQuery() != null) { // condition by id
            queryBuilder.append(" and lower(p.title) like : query ");
            params.put("query", "%" + dto.getQuery().toLowerCase() + "%");
        }


        StringBuilder selectQueryBuilder = new StringBuilder("Select p from PostEntity p ");
        selectQueryBuilder.append(queryBuilder);
        selectQueryBuilder.append(" order by p.createdDate desc ");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT count(p) FROM PostEntity p ");
        countQueryBuilder.append(queryBuilder);


        //select
        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        selectQuery.setFirstResult((page) * size); // offset 50
        selectQuery.setMaxResults(size); // limit 30
        params.forEach(selectQuery::setParameter);
        //count
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());
        params.forEach(countQuery::setParameter);
        Long totalCount = (Long) countQuery.getSingleResult();

        List<PostEntity> postEntityList = selectQuery.getResultList();

        return new FilterResultDTO<>(postEntityList, totalCount);
    }


    public FilterResultDTO<Object[]> filter(PostAdminFilterDTO dto, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder(" where p.visible = true ");

        Map<String, Object> params = new HashMap<>();


        if (dto.getProfileQuery() != null&& !dto.getProfileQuery().isBlank()) { // condition by name or username
            queryBuilder.append(" and (lower(pr.name) like  :profileQuery or lower(pr.username) like :profileQuery) ");
            params.put("profileQuery", "%" + dto.getProfileQuery().toLowerCase() + "%");
        }
        if (dto.getPostQuery() != null&& !dto.getPostQuery().isBlank()) { // condition by id or title
            queryBuilder.append(" and (lower(p.id) = :postId or lower(p.title) like :postQuery) ");
            params.put("postId",   dto.getPostQuery().toLowerCase());
            params.put("postQuery", "%" + dto.getPostQuery().toLowerCase() + "%");
        }


        StringBuilder selectQueryBuilder = new StringBuilder("Select p.id as postId, p.photoId as postPhotoId, p.title as postTitle, p.createdDate as postCreatedDate, " +
                "pr.id as profileId , pr.name as profileName, pr.username as profileUsername ")
        .append(" from PostEntity p ")
        .append(" inner join p.profile as pr ")
        .append(queryBuilder)
        .append(" order by p.createdDate desc ");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT count(p)  FROM PostEntity p inner join p.profile as pr ");
        countQueryBuilder.append(queryBuilder);


        //select
        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        selectQuery.setFirstResult(page * size); // offset 50
        selectQuery.setMaxResults(size); // limit 30
        params.forEach((k,v) -> {
            selectQuery.setParameter(k,v);
        });
        //count
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());
        params.forEach((k,v) -> {
            countQuery.setParameter(k,v);
        });


        Long totalCount = (Long) countQuery.getSingleResult();
        List<Object[]> postEntityList = selectQuery.getResultList();


        return new FilterResultDTO<>(postEntityList, totalCount);
    }

}
