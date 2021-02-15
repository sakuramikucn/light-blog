package cn.sakuramiku.lightblog.common.util;

import cn.sakuramiku.lightblog.common.ResultPage;

import java.util.Collections;
import java.util.List;

/**
 * 简单的分页结果实现
 *
 * @author lyy
 */
public class SimpleResultPage<E> implements ResultPage<E> {

    /**
     * 默认第1页
     */
    protected static final int DEFAULT_PAGE = 1;
    /**
     * 默认每页10项
     */
    protected static final int DEFAULT_PAGE_SIZE = 10;

    protected int page;

    protected int pageSize;

    protected int count;

    protected int pageCount;

    protected List<E> items;

    protected SimpleResultPage() {
        items = Collections.emptyList();
    }

    public SimpleResultPage(List<E> items) {
        this.items = items;
        this.page = DEFAULT_PAGE;
        this.pageSize = items.size();
        this.count = items.size();
        this.pageCount = 1;
    }

    public SimpleResultPage(List<E> items, int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        this.count = items.size();
        this.pageCount = calcPageCount();
        this.items = split(items);
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int getPageCount() {
        return pageCount;
    }

    @Override
    public List<E> getItems() {
        return items;
    }

    @Override
    public ResultPage<E> transResultPage(List<E> items) {
        if (null == items) {
            return this;
        }
        return new SimpleResultPage<>(items);
    }

    @Override
    public ResultPage<E> transResultPage(List<E> items, int page, int pageSize) {
        // 分不了页，初始化一下数据就返回
        if (null == items || 0 == items.size() || 0 == page || 0 == pageSize) {
            SimpleResultPage<E> resultPage = new SimpleResultPage<>();
            resultPage.count = null == items ? 0 : items.size();
            resultPage.pageCount = calcPageCount();
            resultPage.page = page;
            resultPage.pageSize = pageSize;
            resultPage.items = Collections.emptyList();
            return resultPage;
        }
        return new SimpleResultPage<>(items, page, pageSize);
    }

    /**
     * 不分页
     *
     * @param items
     * @return
     */
    public static <E> ResultPage<E> valueOf(List<E> items) {
        return new SimpleResultPage<E>().transResultPage(items);
    }

    /**
     * 分页
     *
     * @param items
     * @param page
     * @param pageSize
     * @return
     */
    @Deprecated
    public static <E> ResultPage<E> valueOf(List<E> items, int page, int pageSize) {
        return new SimpleResultPage<E>().transResultPage(items, page, pageSize);
    }

    /**
     * 简单包装
     *
     * @param items     已分页数据
     * @param page      第几页
     * @param pageSize  每页项数
     * @param count     总项数
     * @param pageCount 分几页
     * @param <E>       数据类型
     * @return 分页对象
     */
    public static <E> ResultPage<E> wrap(List<E> items, int page, int pageSize, int count, int pageCount) {
        SimpleResultPage<E> result = new SimpleResultPage<>();
        result.items = items;
        result.page = page;
        result.pageSize = pageSize;
        result.count = count;
        result.pageCount = pageCount;
        return result;
    }

    /**
     * 逻辑分页
     *
     * @param items
     * @return
     */
    @Deprecated
    public List<E> split(List<E> items) {
        if (page > pageCount) {
            return Collections.emptyList();
        }
        int fromIndex = 0;
        if (1 != page) {
            // page=2,pageSize=10 => 10
            fromIndex = (page - 1) * pageSize;
        }
        // page=2,pageSize=10 => 20 (索引20是不包含的)
        int toIndex = page * pageSize;
        // 最后一页，元素数量不够，就按元素数量切
        if (toIndex > count - 1) {
            toIndex = count;
        }
        // 列表索引从0开始哦
        return items.subList(fromIndex, toIndex);
    }

    /**
     * 计算可以分多少页
     *
     * @return
     */
    private int calcPageCount() {
        if (0 == pageSize) {
            return 0;
        }
        return 0 == count % pageSize ? count / pageSize : count / pageSize + 1;
    }
}
