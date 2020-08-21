package eblog.demo.config;


import com.example.template.TimeAgoMethod;
import eblog.demo.template.PostsTemplate;
import eblog.demo.template.RankTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;


    @Autowired
    PostsTemplate postsTemplate;

    @Autowired
    RankTemplate rankTemplate;

    @PostConstruct
    public void setUp(){
        configuration.setSharedVariable("timeAgo",new TimeAgoMethod());
        configuration.setSharedVariable("posts",postsTemplate);
        configuration.setSharedVariable("hots",rankTemplate);
    }


}
