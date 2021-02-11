package cn.sakuramiku.lightblog.common;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果接口
 *
 * @author lyy
 */
public interface ResultPage<E> extends Serializable {

    /**
     * 第几页
     *
     * @return
     */
    int getPage();

    /**
     * 每页多少项
     *
     * @return
     */
    int getPageSize();

    /**
     * 总项数
     *
     * @return
     */
    int getCount();

    /**
     * 分几页
     *
     * @return
     */
    int getPageCount();

    /**
     * 当前页元素
     *
     * @return
     */
    List<E> getItems();

    /**
     * List转为结果页对象
     *
     * @param items List集合
     * @return 结果页对象
     */

    ResultPage<E> transResultPage(List<E> items);

    /**
     * List转为结果页对象，并分页
     *
     * @param items    List集合
     * @param page     第几页
     * @param pageSize 每页项数
     * @return 分好页的结果页对象
     */
    ResultPage<E> transResultPage(List<E> items, int page, int pageSize);

}
