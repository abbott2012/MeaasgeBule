package com.guoji.mobile.cocobee.callback;

import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.EncryptionUtils;
import com.guoji.mobile.cocobee.utils.Utils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;

/**
 * Created by _H_JY on 2017/3/12.
 */
public abstract class StringComCallback extends StringCallback {

    public StringComCallback() {

    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        User userLoginInfo = Utils.getUserLoginInfo();
        if (userLoginInfo != null) {
            HttpParams params = new HttpParams();
            String encoding = EncryptionUtils.encoding(userLoginInfo.getPid(), System.currentTimeMillis() + "");
            params.put("identity", encoding);
            request.params(params);
        }
    }
}
