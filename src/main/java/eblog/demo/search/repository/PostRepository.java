package eblog.demo.search.repository;

import eblog.demo.search.model.PostDocument;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends ElasticsearchCrudRepository<PostDocument,Long> {

    // 符合jpa命名规范的接口
    Page<PostDocument> search(QueryBuilder queryBuilder, Pageable pageable);

}
