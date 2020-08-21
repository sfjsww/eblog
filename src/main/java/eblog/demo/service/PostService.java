package eblog.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import eblog.demo.vo.PostVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-29
 */
public interface PostService extends IService<Post> {
    IPage paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    PostVo selectOnePost(QueryWrapper<Post> wrapper);

    void initWeekRank();

    void incrCommentCountAndUnionForRank(long postId,boolean isIncr);

    void putViewCount(PostVo vo);
}
