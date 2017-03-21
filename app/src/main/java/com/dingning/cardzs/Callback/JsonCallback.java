package com.dingning.cardzs.Callback;

/**
 * Created by Allen on 2016/12/13.
 */


import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.model.BaseResponse;
import com.dingning.cardzs.model.SimpleResponse;
import com.dingning.cardzs.utils.Convert;
import com.dingning.cardzs.utils.Key;
import com.dingning.cardzs.utils.Md5Utils;
import com.dingning.cardzs.utils.StringUtils;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;


public abstract class JsonCallback<T> extends AbsCallback<T> {

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //"str":"card_id=3526489650&key=yudianedutest","sign":"332684f0d4275f1a797e5734a37e94a0"
        HttpParams params = request.getParams();
        params.put(Parameter.KEY, Key.key);
        String param = StringUtils.filterParams(request.getParams().toString());
        String sign = Md5Utils.MD5_32bit(param);
        params.remove(Parameter.KEY);
        params.put(Parameter.SIGN, sign);
    }


    @Override
    public T convertSuccess(Response response) throws Exception {

        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];

        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");

        // JsonReader jsonReader = new JsonReader(response.body().charStream());
        String responseStr = response.body().string();

        Type rawType = ((ParameterizedType) type).getRawType();
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        //以下代码是根据泛型解析数据，返回对象，返回的对象自动以参数的形式传递到 onSuccess 中，可以直接使用
        if (typeArgument  == Void.class) {
            //无数据类型,表示没有data数据的情况（以  new DialogCallback<LzyResponse<Void>>(this)  以这种形式传递的泛型)
            SimpleResponse simpleResponse = Convert.fromJson(responseStr, SimpleResponse.class);
            response.close();
            //noinspection unchecked
            return (T) simpleResponse.toLzyResponse();
        } else if (rawType == BaseResponse.class) {
            //有数据类型，表示有data
            BaseResponse result = Convert.fromJson(responseStr, type);
            int code = result.status;
            response.close();
            //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改\

            if (code == 0) {
                return (T) result;
            }else if (code == -1) {
                //比如：用户授权信息无效，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("系统繁忙，稍后再试");
            } else if (code == 1000) {
                throw new IllegalStateException("处理失败");
            } else if (code == 1001) {
                throw new IllegalStateException("access_token非法");
            } else if (code == 1002) {
                throw new IllegalStateException("sign验证失败");
            } else if (code == 1003) {
                throw new IllegalStateException("请求参数缺失");
            } else if (code == 1004) {
                throw new IllegalStateException("无操作权限");
            } else if (code == 1005) {
                throw new IllegalStateException("账号或密码不正确");
            } else if (code == 1006) {
                throw new IllegalStateException("账号被冻结");
            } else {
                throw new IllegalStateException("错误代码：" + code + "，错误信息：" + result.info);
            }
        } else {
            response.close();
            throw new IllegalStateException("基类错误无法解析!");
        }
    }
}
