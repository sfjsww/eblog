package eblog.demo;

import eblog.demo.entity.User;
import eblog.demo.service.PostService;
import eblog.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class  DemoApplicationTests{
    @Autowired
    PostService postService;

    @Test
    public void contextLoads(){
    }

}
