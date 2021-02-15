package cn.sakuramiku.lightblog.util;

import cn.sakuramiku.lightblog.common.ResultPage;
import cn.sakuramiku.lightblog.common.util.SimpleResultPage;
import cn.sakuramiku.lightblog.vo.TransView;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目辅助类
 *
 * @author lyy
 */
public class BlogHelper {

    public static String genReqUrl(String method, String url) {
        return method + " " + url;
    }

    /**
     * 将{@link PageInfo}包装成 {@code ResultPage}
     *
     * @param items 数据
     * @param info  分页信息
     * @param <E>   数据类型
     * @return {@code ResultPage}
     */
    public static <E, T> ResultPage<E> wrap(List<E> items, PageInfo<T> info) {
        return SimpleResultPage.wrap(items, info.getPageNum(), info.getPageSize(), info.getSize(), (int) info.getTotal());
    }

    /**
     * 将{@link PageInfo}包装成 {@code ResultPage}
     *
     * @param info 分页信息
     * @param <E>  数据类型
     * @return {@code ResultPage}
     */
    public static <E> ResultPage<E> wrap(PageInfo<E> info) {
        return SimpleResultPage.wrap(info.getList(), info.getPageNum(), info.getPageSize(), info.getSize(), (int) info.getTotal());
    }

    /**
     * 简单转换一下
     *
     * @param origin
     * @param targetClazz
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T extends TransView<S, T>> PageInfo<T> trans(PageInfo<S> origin, Class<T> targetClazz) {
        List<T> views = origin.getList().parallelStream().map(o -> {
            try {
                return targetClazz.newInstance().valueOf(o);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());

        PageInfo<T> info = new PageInfo<>();
        info.setList(views);
        info.setPageNum(origin.getPageNum());
        info.setPageSize(origin.getPageSize());
        info.setTotal(origin.getTotal());
        info.setPages(origin.getPages());
        info.setSize(origin.getSize());
        info.setHasNextPage(origin.isHasNextPage());
        info.setHasPreviousPage(origin.isHasPreviousPage());
        info.setIsFirstPage(origin.isIsFirstPage());
        info.setHasPreviousPage(origin.isIsLastPage());
        info.setNavigateFirstPage(origin.getNavigateFirstPage());
        info.setNavigateLastPage(origin.getNavigateLastPage());
        info.setNavigatepageNums(origin.getNavigatepageNums());
        info.setNavigatePages(origin.getNavigatePages());
        info.setStartRow(origin.getStartRow());
        info.setNextPage(origin.getNextPage());
        info.setPrePage(origin.getPrePage());
        info.setEndRow(origin.getEndRow());
        return info;
    }

}
