package eblog.demo.shiro;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AccountProfile implements Serializable {

    private String username;
    private String email;
    private String sign;

    private Long id;
    private String gender;
    private String avatar;
    private Date created;

    public String getSex(){
        return "0".equals(gender)?"女":"男";
    }
}
