package org.jfox.mvc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Arrays;

/**
 * 支持分页的List
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class PagedList<T> extends ArrayList<T>{

    /**
     * 所有的数据
     */
    private List<T> allDatas;

    /**
     * 当前页的数据
     */
    private List<T> pageDatas;

    /**
     * 每页的数据数
     */
    private int pageSize;

    /**
     * 当前页码
     */
    private int index;

    /**
     * @param pageSize per data of page
     */
    public PagedList(int pageSize) {
        this(0, pageSize);
    }


    /**
     * Constructor to set the initial size and the page size
     *
     * @param initialCapacity - the initial size
     * @param pageSize        - the page size
     */
    public PagedList(int initialCapacity, int pageSize) {
        this.pageSize = pageSize;
        this.index = 0;
        this.allDatas = new ArrayList<T>(initialCapacity);
        repaginate();
    }

    /**
     * Constructor to create an instance using an existing collection
     *
     * @param c        - the collection to build the instance with
     * @param pageSize - the page size
     */
    public PagedList(Collection<T> c, int pageSize) {
        this.pageSize = pageSize;
        this.index = 0;
        this.allDatas = new ArrayList<T>(c);
        repaginate();
    }

    private void repaginate() {
        if (allDatas.isEmpty()) {
            pageDatas = Collections.emptyList();
        }
        else {
            int start = index * pageSize;
            int end = start + pageSize - 1;
            if (end >= allDatas.size()) {
                end = allDatas.size() - 1;
            }
            if (start >= allDatas.size()) {
                index = 0;
                repaginate();
            }
            else if (start < 0) {
                index = allDatas.size() / pageSize;
                if (allDatas.size() % pageSize == 0) {
                    index--;
                }
                repaginate();
            }
            else {
                pageDatas = allDatas.subList(start, end + 1);
            }
        }
    }

    /* List accessors (uses page) */

    public int size() {
        return pageDatas.size();
    }

    public boolean isEmpty() {
        return pageDatas.isEmpty();
    }

    public boolean contains(Object o) {
        return pageDatas.contains(o);
    }

    public Iterator<T> iterator() {
        return pageDatas.iterator();
    }

    public Object[] toArray() {
        return pageDatas.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return pageDatas.toArray(a);
    }

    public boolean containsAll(Collection<?> c) {
        return pageDatas.containsAll(c);
    }

    public T get(int index) {
        return pageDatas.get(index);
    }

    public int indexOf(Object o) {
        return pageDatas.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return pageDatas.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return pageDatas.listIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return pageDatas.listIterator(index);
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return pageDatas.subList(fromIndex, toIndex);
    }

    /* List mutators (uses master list) */

    public boolean add(T o) {
        boolean b = allDatas.add(o);
        repaginate();
        return b;
    }

    public boolean remove(Object o) {
        boolean b = allDatas.remove(o);
        repaginate();
        return b;
    }

    public boolean addAll(Collection<? extends T> c) {
        boolean b = allDatas.addAll(c);
        repaginate();
        return b;
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        boolean b = allDatas.addAll(index, c);
        repaginate();
        return b;
    }

    public boolean removeAll(Collection<?> c) {
        boolean b = allDatas.removeAll(c);
        repaginate();
        return b;
    }

    public boolean retainAll(Collection c) {
        boolean b = allDatas.retainAll(c);
        repaginate();
        return b;
    }

    public void clear() {
        allDatas.clear();
        repaginate();
    }

    public T set(int index, T element) {
        T o = allDatas.set(index, element);
        repaginate();
        return o;
    }

    public void add(int index, T element) {
        allDatas.add(index, element);
        repaginate();
    }

    public T remove(int index) {
        T o = allDatas.remove(index);
        repaginate();
        return o;
    }

    /* Paginated methods */

    public int getPageSize() {
        return pageSize;
    }

    public boolean isFirstPage() {
        return index == 0;
    }

    public boolean isMiddlePage() {
        return !(isFirstPage() || isLastPage());
    }

    public boolean isLastPage() {
        return allDatas.size() - ((index + 1) * pageSize) < 1;
    }

    public boolean hasNextPage() {
        return !isLastPage();
    }

    public boolean hasPreviousPage() {
        return !isFirstPage();
    }

    public boolean nextPage() {
        if (hasNextPage()) {
            index++;
            repaginate();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean previousPage() {
        if (hasPreviousPage()) {
            index--;
            repaginate();
            return true;
        }
        else {
            return false;
        }
    }

    public void gotoPage(int index) {
        if (index < 0) {
            index = 0;
        }
        this.index = index;
        repaginate();
    }

    public int getPageIndex() {
        return index;
    }

    public int countPages() {
        int pageCount = allDatas.size() / pageSize;
        if (allDatas.size() % pageSize != 0) {
            pageCount++;
        }
        return pageCount;
    }

    public static void main(String[] args) {

        PagedList<String> pagedList = new PagedList<String>(4);

        for (int i = 0; i < 9; i++) {
            pagedList.add("" + i);
        }
        System.out.println(pagedList.countPages());

        do{
            System.out.println(Arrays.toString(pagedList.toArray(new String[pagedList.size()])));
        }
        while (pagedList.nextPage());
    }
}
