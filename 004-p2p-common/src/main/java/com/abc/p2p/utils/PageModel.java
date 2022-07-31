package com.abc.p2p.utils;

import java.io.Serializable;

public class PageModel implements Serializable {
    //每页显示条数
    private Integer pageContent;

    //总条数
    private Integer totalCount;

    //当前页
    private Integer curPage;

    //首页
    private Integer firstPage = 1;

    //尾页
    private Integer lastPage;




    public PageModel(Integer pageContent) {
        this.pageContent = pageContent;
    }


    public Integer getPageContent() {
        return pageContent;
    }

    public void setPageContent(Integer pageContent) {
        this.pageContent = pageContent;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public Integer getLastPage() {

        lastPage = totalCount%pageContent==0?totalCount/pageContent:totalCount/pageContent+1;
        return lastPage;
    }

}
