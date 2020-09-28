package eblog.demo.common.exception;

import cn.hutool.json.JSONUtil;
import eblog.demo.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handler(HttpServletRequest req, HttpServletResponse response,Exception e) throws IOException {

//        ajax处理
        String header = req.getHeader("X-Requested-With");
        if (header!=null && "XMLHttpRequest".equals(header)){
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSONUtil.toJsonStr(Result.fail(e.getMessage())));

            return null;
        }

//        web处理
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message",e.getMessage());
        modelAndView.addObject("currentCategoryId",0);
        return modelAndView;

    }
}
