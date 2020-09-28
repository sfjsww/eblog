package eblog.demo.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import eblog.demo.common.lang.Result;
import eblog.demo.entity.Post;
import eblog.demo.entity.User;
import eblog.demo.entity.UserMessage;
import eblog.demo.shiro.AccountProfile;
import eblog.demo.util.UploadUtil;
import eblog.demo.vo.UserMessageVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import eblog.demo.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.lang.management.LockInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-29
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    UploadUtil uploadUtil;


    @GetMapping("/user/home")
    public String home(){

        User user = userService.getById(getProfileId());
        System.out.println(getProfileId());

        List<Post> posts = postService.list(new QueryWrapper<Post>()
            .eq("user_id",getProfileId())
//                30天内的文章
//            .gt("created", DateUtil.offsetDay(new Date(),-30))
            .orderByDesc("created")
        );
        req.setAttribute("user",user);
        req.setAttribute("posts",posts);

        return "/user/home";
    }

    @GetMapping("/user/set")
    public String set(){
        User user = userService.getById(getProfileId());

        req.setAttribute("user",user);

        return "/user/set";
    }

    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(User user){

        if (StrUtil.isNotBlank(user.getAvatar())){
            User temp = userService.getById(getProfileId());
            temp.setAvatar(user.getAvatar());
            userService.updateById(temp);

            AccountProfile profile = getProfile();
            profile.setAvatar(user.getAvatar());

            SecurityUtils.getSubject().getSession().setAttribute("profile",profile);


            return Result.success().action("/user/set#avatar");
        }

        if (StrUtil.isBlank(user.getUsername())){
            return Result.fail("昵称不能为空");
        }
        int count = userService.count(new QueryWrapper<User>()
                .eq("username",getProfile().getUsername())
                .ne("id",getProfileId()));
        if (count > 0){
            return Result.fail("该昵称已被占用");
        }

        User temp = userService.getById(getProfileId());
        temp.setUsername(user.getUsername());
        temp.setGender(user.getGender());
        temp.setSign(user.getSign());
        userService.updateById(temp);

        AccountProfile profile = getProfile();
        profile.setUsername(temp.getUsername());
        profile.setSign(temp.getSign());

        SecurityUtils.getSubject().getSession().setAttribute("profile",profile);


        return Result.success().action("/user/set");
    }


    @ResponseBody
    @PostMapping("/user/upload")
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtil.upload(UploadUtil.type_avatar,file);


    }

    @ResponseBody
    @PostMapping("/user/repass")
    public Result repass(String nowpass,String pass,String repass) {
        if (!pass.equals(repass)){
            return Result.fail("两次密码不相同");
        }

        User user = userService.getById(getProfileId());
        String userPassWord = user.getPassword();
        String passMd5 = SecureUtil.md5(pass);

        String nowPassMd5 = SecureUtil.md5(nowpass);

        if (!nowPassMd5.equals(user.getPassword())){
            return Result.fail("当前密码输入错误");
        }
        if (userPassWord.equals(passMd5)){
            System.out.println(userPassWord);
            System.out.println(passMd5);
            return Result.fail("新旧密码不能相同");
        }

        user.setPassword(passMd5);
        userService.updateById(user);
        Result result =  Result.success("密码修改成功");
        result.setAction("/user/set#pass");
        return result;
    }


    @GetMapping("/user/index")
    public String index(){
        User user = userService.getById(getProfileId());

        return "/user/index";
    }

    @ResponseBody
    @GetMapping("/user/collection")
    public Result collection(){
        IPage page = postService.page(getPage(),new QueryWrapper<Post>()
                .inSql("id","SELECT post_id FROM user_collection where user_id=" + getProfileId())
        );


        return Result.success(page);
    }

    @ResponseBody
    @GetMapping("/user/public")
    public Result userPublic(){
        IPage page = postService.page(getPage(),new QueryWrapper<Post>()
                .eq("user_id",getProfileId())
                .orderByDesc("created"));




        return Result.success(page);
    }

    @GetMapping("/user/message")
    public String message() {
        IPage<UserMessageVo> page = messageService.paging(getPage(), new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .orderByDesc("created")
        );

        List<Long> ids = new ArrayList<>();
        for (UserMessageVo messageVo:page.getRecords()){
            if (messageVo.getStatus() == 0){
                ids.add(messageVo.getId());
            }
        }

        //批量修改成已读
        messageService.updateToReed(ids);

        req.setAttribute("pageData",page);

        return "/user/message";
    }


    @ResponseBody
    @PostMapping("/message/remove/")
    public Result msgRemove(Long id,@RequestParam(defaultValue = "false") Boolean all){


        boolean remove = messageService.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id",getProfileId())
                .eq(!all,"id",id));


        return remove? Result.success(null):Result.fail("删除失败");

    }

    @ResponseBody
    @RequestMapping("/message/nums/")
    public Map msgNums(){

        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id",getProfileId())
                .eq("status",0)
        );

        return MapUtil.builder("status",0).put("count",count).build();

    }


}
