package eblog.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import eblog.demo.vo.CommentVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-29
 */
public interface CommentService extends IService<Comment> {

    IPage<CommentVo> paging(Page page, Long postId, Long userId, String order);
}
