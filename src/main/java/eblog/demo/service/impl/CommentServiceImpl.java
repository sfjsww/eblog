package eblog.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.entity.Comment;
import eblog.demo.mapper.CommentMapper;
import eblog.demo.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import eblog.demo.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-29
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Override
    public IPage<CommentVo> paging(Page page, Long postId, Long userId, String order) {
        return commentMapper.selectComments(page,new QueryWrapper<Comment>()
                .eq(postId !=null,"post_id",postId)
                .eq(userId != null,"user_id",userId)
                .orderByDesc(order != null,order)
        );
    }
}
