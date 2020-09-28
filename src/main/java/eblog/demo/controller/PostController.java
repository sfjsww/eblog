package eblog.demo.controller;




import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import eblog.demo.common.lang.Result;
import eblog.demo.entity.*;
import eblog.demo.service.PostService;
import eblog.demo.util.ValidationUtil;
import eblog.demo.vo.CommentVo;
import eblog.demo.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

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

//    判断用户是否收藏了文章
    @ResponseBody
    @PostMapping("/collection/find/")
    public Result collectionFind(Long pid){

        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id",getProfileId())
                .eq("post_id",pid)
        );

        return Result.success(MapUtil.of("collection",count>0));
    }

    @ResponseBody
    @PostMapping("/collection/add/")
    public Result collectionAdd(Long pid){

        Post post = postService.getById(pid);
        Assert.isTrue(post!=null,"该帖子已被删除");
        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id",getProfileId())
                .eq("post_id",pid)
        );
        if (count>0){
            return Result.fail("该文章已被您收藏");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(getProfileId());
        collection.setPostId(pid);
        collection.setCreated(new Date());
        collection.setModified(new Date());
        collection.setPostUserId(post.getUserId());

        userCollectionService.save(collection);

        return Result.success();
    }

    @ResponseBody
    @PostMapping("/collection/remove/")
    public Result collectionRemove(Long pid){

        Post post = postService.getById(pid);
        Assert.isTrue(post!=null,"该帖子已被删除");

        userCollectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id",getProfileId())
                .eq("post_id",pid)
        );

        return Result.success();
    }

    @GetMapping("/post/edit")
    public String edit(){

        String id = req.getParameter("id");
        if (!StringUtils.isEmpty(id)){
            Post post = postService.getById(id);
            Assert.isTrue(post!=null,"该帖子已被删除");
            Assert.isTrue(post.getUserId().equals(getProfileId()),"没有权限操作此文章");
            req.setAttribute("post",post);
        }
        req.setAttribute("categories",categoryService.list());
        return "/post/edit";
    }

    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(Post post){
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if (validResult.hasErrors()){
            return Result.fail(validResult.getErrors());
        }

        if (post.getId()==null){
            post.setUserId(getProfileId());
            post.setModified(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);
            postService.save(post);
        }else {
            Post tempPost = postService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().equals(getProfileId()),"无权限编辑该文章！");

            tempPost.setTitle(post.getTitle());
            tempPost.setContent(post.getContent());
            tempPost.setCategoryId(post.getCategoryId());

            postService.updateById(tempPost);

        }

        return Result.success().action("/post/" + post.getId());


    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/delete")
    public Result delete(Long id){
        Post post = postService.getById(id);

        Assert.notNull(post,"该帖子已被删除");
        Assert.isTrue(post.getUserId().longValue()==getProfileId().longValue(),"无权限删除此文章！");

        postService.removeById(id);

//        删除相关信息,收藏等
        messageService.removeByMap(MapUtil.of("post_id",id));
        userCollectionService.removeByMap(MapUtil.of("post_id",id));
        postService.removeHots(id);

        Result result = Result.success("删除成功",null);
        result.setAction("/user/index");
        return result;
    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/reply/")
    public Result reply(Long jid,String content){
        Assert.notNull(jid,"找不到对应的文章！");
        Assert.notNull(content,"评论内容不能为空！");

        Post post = postService.getById(jid);
        Assert.isTrue(post!=null,"该文章已被删除！");

        Comment comment = new Comment();
        comment.setPostId(jid);
        comment.setContent(content);
        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setLevel(0);
        comment.setVoteUp(0);
        comment.setVoteDown(0);
        commentService.save(comment);
//      评论数量加一
        post.setCommentCount(post.getCommentCount() + 1);
        postService.updateById(post);

//        本周热议数量加一
        postService.incrCommentCountAndUnionForRank(post.getId(),true);

//        通知作者，有人评论文章
//        除了作者自己评论之外。
        if (!comment.getUserId().equals(post.getUserId())){
            UserMessage message = new UserMessage();
            message.setPostId(jid);
            message.setCommentId(comment.getId());
            message.setFromUserId(getProfileId());
            message.setToUserId(post.getUserId());
            message.setType(1);
            message.setContent(content);
            message.setCreated(new Date());
            message.setStatus(0);

            messageService.save(message);

//            Websocket实时通讯告诉作者
            wsService.sendMessCountToUser(message.getToUserId());
        }

//        通知被@的人，有人回复了评论。
        if (content.startsWith("@")){
            String username = content.substring(1,content.indexOf(" "));
            System.out.println(username);
            if (!username.equals(getProfile().getUsername())){
                User user = userService.getOne(new QueryWrapper<User>()
                        .eq("username",username)
                );
                if (user!=null){
                    UserMessage message = new UserMessage();
                    message.setPostId(jid);
                    message.setCommentId(comment.getId());
                    message.setFromUserId(getProfileId());
                    message.setToUserId(user.getId());
                    message.setType(2);
                    message.setContent(content);
                    message.setCreated(new Date());
                    message.setStatus(0);

                    messageService.save(message);
                }
            }


        }
//        Websocket实时通讯通知


        return Result.success().action("/post/" + post.getId());
    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/jieda-delete/")
    public Result reply(Long id) {

        Assert.notNull(id, "评论id不能为空！");

        Comment comment = commentService.getById(id);

        Assert.notNull(comment, "找不到对应评论！");

        if(comment.getUserId().longValue() != getProfileId().longValue()) {
            return Result.fail("不是你发表的评论！");
        }
        commentService.removeById(id);

        // 评论数量减一
        Post post = postService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        postService.saveOrUpdate(post);

        //评论数量减一
        postService.incrCommentCountAndUnionForRank(comment.getPostId(), false);

        return Result.success(null);
    }
}
