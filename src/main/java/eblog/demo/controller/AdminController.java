package eblog.demo.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.common.lang.Result;
import eblog.demo.entity.Post;
import eblog.demo.vo.PostVo;
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

    @ResponseBody
    @PostMapping("/initEsData")
    public Result initEsData(){

        int size = 10000;
        Page page = new Page();
        page.setSize(size);

        long total = 0;

        for (int i = 1;i < 1000;i++){
            page.setCurrent(i);

            IPage<PostVo> paging = postService.paging(page, null, null, null, null, null);
            int num = searchService.initEsData(paging.getRecords());

            total += num;

//            一页不足10000条时，终止循环。
            if (paging.getRecords().size() < size){
                break;
            }

        }

        return Result.success("ES索引初始化成功，共" + total + "条记录",null);
    }

}
