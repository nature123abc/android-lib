package com.dk.lib_dk.utils.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.nicesky.retrofit2_adapter_rxjava3.RxJava3CallAdapterFactory;

/**
 * 可以切换url的管理工具类
 * https://github.com/chenpdsu/XRetrofit/blob/master/xretrofit/src/main/java/com/msy/xretrofit/XRetrofit.java
 */
public class RetrofitManager {
    public static int outTime = 15;//秒
    private Retrofit mRetrofit;

    private Map<String, Retrofit> retrofitMap = new HashMap<>();
    private Map<String, OkHttpClient> clientMap = new HashMap<>();


    //私有构造
    private RetrofitManager() {
    }

    //静态内部类获取单例
    public static RetrofitManager getInstance() {
        return SingletonHolder.sXRetrofit;
    }

    /**
     * 内部类
     */
    private static class SingletonHolder {
        private static RetrofitManager sXRetrofit = new RetrofitManager();
    }


    public static <S> S get(String baseUrl, Class<S> service) {
        return getInstance().getRetrofit(baseUrl).create(service);
    }


    /**
     * 默认使用rxjava
     *
     * @param baseUrl
     * @return
     */
    public Retrofit getRetrofit(String baseUrl) {
        return getRetrofit(baseUrl, true);
    }

    List<Interceptor> inters = new ArrayList<>();

    public void setInterceptor(List<Interceptor> list) {
        inters = new ArrayList<>();
    }

    public Retrofit getRetrofit(String baseUrl, boolean useRx) {
        if (baseUrl == null || baseUrl.length() == 0) {
            throw new IllegalStateException("base url can not be null!");
        }
        if (retrofitMap.get(baseUrl) != null) {
            return retrofitMap.get(baseUrl);
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl);
        OkHttpClient okHttpClient = getClient(baseUrl, inters);
        builder.client(okHttpClient);
        builder.addConverterFactory(GsonConverterFactory.create());
        if (useRx) {
            builder.addCallAdapterFactory(RxJava3CallAdapterFactory.create());
        }
        Retrofit retrofit = builder.build();
        retrofitMap.put(baseUrl, retrofit);
        clientMap.put(baseUrl, okHttpClient);
        return retrofit;
    }


    public class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return (Converter<ResponseBody, Object>) body -> {
                long contentLength = body.contentLength();
                if (contentLength == 0) {
                    return null;
                }
                return delegate.convert(body);
            };
        }
    }

    private OkHttpClient getClient(String baseUrl, List<Interceptor> list) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(outTime, TimeUnit.SECONDS)
                .readTimeout(outTime, TimeUnit.SECONDS);
        for (Interceptor i : list) {
            builder.addInterceptor(i);//调用自己写的拦截器
        }
        OkHttpClient okHttpClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)//设置BaseUrl
                .client(okHttpClient)//设置请求的client
                .addConverterFactory(new NullOnEmptyConverterFactory()) //必须是要第一个
                .addConverterFactory(GsonConverterFactory.create())//设置转换器
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())//只有使用这个才能使用Observe
                .build();
        return okHttpClient;
    }

    //清除缓存
    public static void clearCache() {
        getInstance().retrofitMap.clear();
        getInstance().clientMap.clear();
    }


    /**
     * 线程切换
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> getScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
