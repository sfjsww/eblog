package eblog.demo.controller;


import cn.hutool.core.map.MapUtil;
import eblog.demo.common.lang.Result;
import eblog.demo.entity.Post;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @ResponseBody
    @PostMapping("/jie-set")
    public Result jieSet(Long id,Integer rank,String field){

        Post post = postService.getById(id);
        Assert.notNull(post,"该帖子已经删除");

        if ("delete".equals(field)){
            postService.removeById(id);
//        删除相关信息,收藏等
            messageService.removeByMap(MapUtil.of("post_id",id));
            userCollectionService.removeByMap(MapUtil.of("post_id",id));
            return Result.success().action("/index");
        }else if ("status".equals(field)){
            post.setRecommend(rank==1);
        }else if ("stick".equals(field)){
            post.setLevel(rank);
        }
        postService.updateById(post);


        return Result.success();
    }

}
