package com.guoji.mobile.cocobee.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/17.
 */

public class QueryRecResponse implements Serializable {

    private String title;
    private List<QueryResponse> mQueryResponseList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QueryResponse> getQueryResponseList() {
        return mQueryResponseList;
    }

    public void setQueryResponseList(List<QueryResponse> queryResponseList) {
        mQueryResponseList = queryResponseList;
    }

   public static class QueryResponse {

        private int pic;
        private String text;

       public int getPic() {
            return pic;
        }

        public void setPic(int pic) {
            this.pic = pic;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
