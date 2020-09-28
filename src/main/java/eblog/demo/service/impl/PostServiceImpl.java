package eblog.demo.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import eblog.demo.entity.Post;
import eblog.demo.mapper.PostMapper;
import eblog.demo.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import eblog.demo.util.RedisUtil;
import eblog.demo.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-29
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    PostMapper postMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {

        if (level == null) {
            level = -1;
        }

        QueryWrapper wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null,"category_id",categoryId)
                .eq(userId != null,"user_id",userId)
                .eq(level == 0,"level",0)
                .gt(level >0,"level",0)
                .orderByDesc(order != null,order);

        return postMapper.selectPosts(page,wrapper);
    }

    @Override
    public PostVo selectOnePost(QueryWrapper<Post> wrapper) {

        return postMapper.selectOnePost(wrapper);
    }

//    本周热议初始化
    @Override
    public void initWeekRank() {

        // 获取7天的发表的文章
        List<Post> posts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -7)) // 11号
                .select("id, title, user_id, comment_count, view_count, created")
        );

        // 初始化文章的总评论量
        for (Post post : posts) {
            String key = "day:rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);

            redisUtil.zSet(key, post.getId(), post.getCommentCount());

            // 7天后自动过期(15号发表，7-（18-15）=4)
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60; // 有效时间

            redisUtil.expire(key, expireTime);


            // 缓存文章的一些基本信息（id，标题，评论数量，作者）
            this.hashCachePostIdAndTitle(post, expireTime);
        }

        // 做并集
        this.zunionAndStoreLast7DayForWeekRank();

    }

    @Override
    public void incrCommentCountAndUnionForRank(long postId, boolean isIncr) {
        String currentKey = "day:rank" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        redisUtil.zIncrementScore(currentKey,postId,isIncr?1: -1);

        Post post = this.getById(postId);

        //            七天后自动过期
        long between = DateUtil.between(new Date(),post.getCreated(), DateUnit.DAY);
        long expireTime = (7 - between) * 24 * 60 * 60;

//        缓存这篇文章的基本信息
        this.hashCachePostIdAndTitle(post,expireTime);


//        重新做并集
        this.zunionAndStoreLast7DayForWeekRank();
    }

    @Override
    public void putViewCount(PostVo vo) {
        String key = "rank:post:" + vo.getId();

//        1.从缓存中获取viewCount
        Integer viewCount = (Integer) redisUtil.hget(key,"post:viewCount");
//        2.如果没有就先从实体里面获取在+1
        if (viewCount!=null){
            vo.setViewCount(viewCount + 1);
        }else {
            vo.setViewCount(vo.getViewCount()+1);
        }
//        3.同步到缓存里
        redisUtil.hset(key,"post:viewCount",vo.getViewCount());

    }

//    刪除本周热议
    @Override
    public void removeHots(Long id) {
        String currentKey = "rank:post:" + id;
        redisUtil.setRemove("Member",id);
        redisUtil.del(currentKey);


//        重新做并集
        this.zunionAndStoreLast7DayForWeekRank();
    }

    /**
     * 评论减少一个
     */


    /**
     * 本周合并评论数量操作
     */
    private void zunionAndStoreLast7DayForWeekRank() {
        String currentKey = "day:rank" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        String destkey = "week:rank";
        List<String> otherKeys = new ArrayList<>();
        for (int i = -6;i<0;i++){
            String temp = "day:rank" + DateUtil.format(DateUtil.offsetDay(new Date(),i),DatePattern.PURE_DATE_FORMAT);
            otherKeys.add(temp);
        }
        redisUtil.zUnionAndStore(currentKey,otherKeys,destkey);
    }

    private void hashCachePostIdAndTitle(Post post, long expireTime) {

        String key = "rank:post:" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if (!hasKey){

            redisUtil.hset(key,"post:id",post.getId(),expireTime);
            redisUtil.hset(key,"post:title",post.getTitle(),expireTime);
            redisUtil.hset(key,"post:commentCount",post.getCommentCount(),expireTime);
            redisUtil.hset(key,"post:viewCount",post.getViewCount(),expireTime);

        }
    }


}
