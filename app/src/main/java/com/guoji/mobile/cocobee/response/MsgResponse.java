package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/15.
 */

public class MsgResponse implements Serializable{

//    post_id true string 历史消息记录主键id
//    post_content true string 消息内容
//    post_type true string 消息类型（1：移动报警2：防拆报警3：低电报警）
//    post_time true string 推送时间

    private String post_id;
    private String post_content;
    private String post_type;
    private String post_time;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }
}
