package eblog.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.entity.UserCollection;
import eblog.demo.service.*;
import eblog.demo.service.impl.PostServiceImpl;
import eblog.demo.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;


public class BaseController {
    @Autowired
    HttpServletRequest req;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    UserMessageService messageService;
    @Autowired
    UserCollectionService userCollectionService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WsService wsService;
    @Autowired
    SearchService searchService;
    @Autowired
    AmqpTemplate amqpTemplate;

    public Page getPage(){
        int pn = ServletRequestUtils.getIntParameter(req,"pn",1);
        int size = ServletRequestUtils.getIntParameter(req,"size",2);

        return new Page(pn,size);

    }

    protected AccountProfile getProfile(){
        return (AccountProfile)SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getProfileId(){
        return getProfile().getId();
    }
}
