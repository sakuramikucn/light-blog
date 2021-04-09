package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Category;
import cn.sakuramiku.lightblog.mapper.CategoryMapper;
import cn.sakuramiku.lightblog.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现
 *
 * @author lyy
 */
@LogConfig(reference = "category",name = "分类")
@CacheConfig(cacheNames = "light_blog:category", keyGenerator = "simpleKeyGenerator")
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @WriteLog(action = WriteLog.Action.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveCategory(String name) {
        long id = IdGenerator.nextId();
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setCreateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        return id;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateCategory(Long id, String name) {
        return categoryMapper.update(id, name);
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeCategory(Long id) {
        return categoryMapper.delete(id);
    }

    @OnChange
    @Cacheable(unless = "null  == #result || 0 == #result.total")
    @Override
    public PageInfo<Category> search(String keyword, LocalDateTime begin, LocalDateTime end, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            PageHelper.startPage(page, pageSize, true);
        }
        List<Category> categories = categoryMapper.search(keyword, begin, end);
        return PageInfo.of(categories);
    }
}
