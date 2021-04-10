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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
    @CachePut(key = "#result.id",unless = "null == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Category saveCategory(String name) {
        long id = IdGenerator.nextId();
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setCreateTime(LocalDateTime.now());
        Boolean insert = categoryMapper.insert(category);
        if (null != insert){
            return this.getCategory(id);
        }
        return null;
    }

    @Cacheable(key = "#id",unless = "null == #result")
    @Override
    public Category getCategory(Long id) {
        return categoryMapper.get(id);
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#result.id",unless = "null == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Category updateCategory(Long id, String name) {
        Boolean update = categoryMapper.update(id, name);
        if (update){
            return this.getCategory(id);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @CacheEvict(key = "#id")
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
