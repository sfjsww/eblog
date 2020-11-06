package eblog.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.search.mq.PostMqIndexMessage;
import eblog.demo.vo.PostVo;

import java.util.List;

public interface SearchService {
    IPage search(Page page, String keyword);

    int initEsData(List<PostVo> records);

    void createOrUpdate(PostMqIndexMessage message);

    void removeIndex(PostMqIndexMessage message);
}
