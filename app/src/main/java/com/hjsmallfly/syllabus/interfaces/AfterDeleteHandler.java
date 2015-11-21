package com.hjsmallfly.syllabus.interfaces;

/**
 * Created by smallfly on 2015/10/30.
 * 用于处理删除数据之后的事情
 */
public interface AfterDeleteHandler {
    public void deal_with_delete(String response, int position);
}
