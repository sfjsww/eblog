package eblog.demo.service;

import eblog.demo.common.lang.Result;
import eblog.demo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import eblog.demo.shiro.AccountProfile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-29
 */
public interface UserService extends IService<User> {

    Result register(User user);

    AccountProfile login(String username, String password);
}
