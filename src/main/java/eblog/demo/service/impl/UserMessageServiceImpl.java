package eblog.demo.service.impl;

import eblog.demo.entity.UserMessage;
import eblog.demo.mapper.UserMessageMapper;
import eblog.demo.service.UserMessageService;
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
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

}
