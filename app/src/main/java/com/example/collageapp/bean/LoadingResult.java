package com.example.collageapp.bean;

/**
 * Created by Yue on 2022/10/27.
 */
public class LoadingResult<T, R> {

    private final LoadingState mState;

    //加载的内容
    private final T mContent;

    //信息 可以传进度
    private final R mMessage;


    //错误信息
    private final String mError;

    private LoadingResult(LoadingState state, T content, R message, String error) {
        mState = state;
        mMessage = message;
        mContent = content;
        mError = error;
    }


    public static <T, R> LoadingResult<T, R> Init() {
        return new LoadingResult<>(LoadingState.INIT, null, null, null);
    }


    public static <T, R> LoadingResult<T, R> Loading(R message) {
        return new LoadingResult<>(LoadingState.LOADING, null, message, null);
    }


    public static <T, R> LoadingResult<T, R> Success(T content) {
        return new LoadingResult<>(LoadingState.SUCCESS, content, null, null);
    }


    public static <T, R> LoadingResult<T, R> Error(String message) {
        return new LoadingResult<>(LoadingState.ERROR, null, null, message);
    }



    public LoadingState getState() {
        return mState;
    }

    public T getContent() {
        return mContent;
    }

    public R getMessage() {
        return mMessage;
    }

    public String getError() {
        return mError;
    }


}
