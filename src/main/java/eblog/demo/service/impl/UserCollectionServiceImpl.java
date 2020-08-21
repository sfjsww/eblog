package eblog.demo.service.impl;

import eblog.demo.entity.UserCollection;
import eblog.demo.mapper.UserCollectionMapper;
import eblog.demo.service.UserCollectionService;
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
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {

}
