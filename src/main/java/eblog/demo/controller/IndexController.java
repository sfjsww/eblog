package eblog.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController{

    @Autowired
    PostService postService;

    @RequestMapping({"","/","index"})
    public String index(){



//        1.分页信息，2.分类，3.user,4.置顶,5.精选,6.排序
        IPage results = postService.paging(getPage(),null,null,null,null,"created");

        req.setAttribute("pageData",results);
        req.setAttribute("currentCategoryId",0);
        return "index";
    }
}
