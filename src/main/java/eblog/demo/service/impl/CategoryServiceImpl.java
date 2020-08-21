package eblog.demo.service.impl;

import eblog.demo.entity.Category;
import eblog.demo.mapper.CategoryMapper;
import eblog.demo.service.CategoryService;
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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
