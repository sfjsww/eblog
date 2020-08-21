package eblog.demo.service.impl;

import eblog.demo.entity.User;
import eblog.demo.mapper.UserMapper;
import eblog.demo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Anvil liu
 * @since 2020-07-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
