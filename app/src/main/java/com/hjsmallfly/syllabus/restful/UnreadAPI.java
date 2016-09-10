package com.hjsmallfly.syllabus.restful;

import com.hjsmallfly.syllabus.pojo.PostList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by smallfly on 16-9-10.
 */
public interface UnreadAPI {
//    interaction/api/v2/unread?uid=1&type=1
//    """
//    测试:
//    参数: uid, type(可选, 0(默认值) - 获取post_id; 1 - 获取对应的 post, 返回值与 get post 返回值一致)
//    curl "localhost:8080/interaction/api/v2/unread?uid=1&type=0"    # type 默认为 0
//    curl "localhost:8080/interaction/api/v2/unread?uid=1&type=1"
//            :return:
//    可能返回值:
//            404, 表示没有该用户, 或者该用户并没有未读信息
//    200, {"messages": [1, 2, 3,...]}, 一系列post_id
//    200, {"post_list": []} post 数组, 同 get post 返回值格式
//    """
    @GET("/interaction/api/v2/unread")
    Call<PostList> getUnreadMessage(@Query("uid") int userID, @Query("type") int type);
}
