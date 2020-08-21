package eblog.demo.controller;


import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class AuthController extends BaseController{

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Autowired
    Producer producer;

    @GetMapping("/kaptcha.jpg")
    public void kaptcha(HttpServletResponse resp) throws IOException {

//        验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        req.getSession().setAttribute(KAPTCHA_SESSION_KEY,text);


        resp.setHeader("Cache-Control","no-store,no-cache");
        resp.setContentType("image/jpeg");
        ServletOutputStream outputStream = resp.getOutputStream();
        ImageIO.write(image,"jpg",outputStream);
    }


    @GetMapping("/login")
    public String login(){
        return "/auth/login";
    }


//    @GetMapping("/login")
//    public String login(){
//
//        String kaptcha = (String) req.getSession().getAttribute(KAPTCHA_SESSION_KEY);
//
//        return "/auth/login";
//    }




    @GetMapping("/register")
    public String register(){
        return "/auth/reg";
    }
}
