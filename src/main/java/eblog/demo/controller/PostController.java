package eblog.demo.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import eblog.demo.entity.Post;
import eblog.demo.service.PostService;
import eblog.demo.vo.CommentVo;
import eblog.demo.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-28
 */
@Controller
public class PostController extends BaseController{

    @Autowired
    PostService postService;

    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id){

        int pn = ServletRequestUtils.getIntParameter(req,"pn",1);


        req.setAttribute("currentCategoryId",id);
        req.setAttribute("pn",pn);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id){

        //1.分页 2.文章id 3.用户id 4.排序


        PostVo vo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id",id));
        Assert.notNull(vo,"文章已删除");

        postService.putViewCount(vo);

        IPage<CommentVo> results = commentService.paging(getPage(),vo.getId(),null,"created");
        req.setAttribute("currentCategoryId",vo.getCategoryId());
        req.setAttribute("post",vo);
        req.setAttribute("pageData",results);
        return "post/detail";
    }

}
