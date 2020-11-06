package eblog.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.entity.Post;
import eblog.demo.search.model.PostDocument;
import eblog.demo.search.mq.PostMqIndexMessage;
import eblog.demo.search.repository.PostRepository;
import eblog.demo.service.PostService;
import eblog.demo.service.SearchService;
import eblog.demo.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PostService postService;

    @Override
    public IPage search(Page page, String keyword) {
        Long current = page.getCurrent() - 1;
        Long size = page.getSize();
//        分页信息 mybatis plus的page转成jpa的page
        Pageable pageable = PageRequest.of(current.intValue(),size.intValue());

//        搜索es得到pageData
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "authorName", "categoryName");
        org.springframework.data.domain.Page<PostDocument> postDocumentPage = postRepository.search(multiMatchQueryBuilder,pageable);
        System.out.println(postDocumentPage);


//        结果信息 jpa的pageData转成mybatis plus的pageData
        IPage pageData = new Page(page.getCurrent(),page.getSize(),postDocumentPage.getTotalElements());
        pageData.setRecords(postDocumentPage.getContent());

        return pageData;
    }

    @Override
    public int initEsData(List<PostVo> records) {

        if (records == null || records.isEmpty()){
            return 0;
        }

        List<PostDocument> documents = new ArrayList<>();

        for (PostVo vo : records){
            PostDocument postDocument = modelMapper.map(vo, PostDocument.class);
            documents.add(postDocument);
        }
        postRepository.saveAll(documents);

        return documents.size();
    }

    @Override
    public void createOrUpdate(PostMqIndexMessage message) {
        Long postId = message.getPostId();
        PostVo postVo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", postId));

        PostDocument postDocument = modelMapper.map(postVo, PostDocument.class);

        postRepository.save(postDocument);

        log.info("es 索引更新成功！ ---> {}",postDocument.toString());
    }

    @Override
    public void removeIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();

        postRepository.deleteById(postId);

        log.info("es 索引删除成功 ---> {}",message.toString());

    }
}
