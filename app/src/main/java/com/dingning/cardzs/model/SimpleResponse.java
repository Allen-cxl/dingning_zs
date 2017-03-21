package com.dingning.cardzs.model;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = 5687944394258164155L;
    public int status;
    public String info;

    public BaseResponse toLzyResponse() {
        BaseResponse lzyResponse = new BaseResponse();
        lzyResponse.status = status;
        lzyResponse.info = info;
        return lzyResponse;
    }
}