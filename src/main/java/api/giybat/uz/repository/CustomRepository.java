package api.giybat.uz.repository;

import api.giybat.uz.dto.FilterResultDTO;
import api.giybat.uz.dto.post.PostDTO;
import api.giybat.uz.dto.post.PostFilterDTO;
import api.giybat.uz.entity.PostEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class CustomRepository {
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

}
