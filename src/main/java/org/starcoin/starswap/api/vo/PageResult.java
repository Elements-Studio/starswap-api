package org.starcoin.starswap.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "Page result")
public class PageResult<T> {

    @ApiModelProperty("Total page")
    private int totalPage = 0;

    @ApiModelProperty("Current page No.")
    private int currentPage = 0;

    @ApiModelProperty("Total elements")
    private long totalElements = 0;

    @ApiModelProperty("Data list")
    private List<T> list = null;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
