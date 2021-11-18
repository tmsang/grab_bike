package com.intec.grab.bike.utils.api;
import com.intec.grab.bike.utils.log.Log;

import android.app.Activity;
import retrofit2.Call;
import retrofit2.Response;

public class Callback<T> {

    public interface ISuccessCallback<T> {
        void onSuccess(T data);
    }

    public interface IErrorCallback {
        void onError(ApiException t);
    }



    private final ISuccessCallback<T> onSuccess;
    private final IErrorCallback onError;

    private Callback(ISuccessCallback<T> onSuccess, IErrorCallback onError) {
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    public static <T> retrofit2.Callback<T> callInUI(
            Activity context,
            ISuccessCallback<T> onSuccess,
            IErrorCallback onError
    ) {
        return call(
            (data) -> context.runOnUiThread(() -> onSuccess.onSuccess(data)),
            (e) -> context.runOnUiThread(() -> onError.onError(e))
        );
    }

    public static <T> retrofit2.Callback<T> call() {
        return call((e) -> {}, (e) -> {});
    }

    public static <T> retrofit2.Callback<T> call(ISuccessCallback<T> onSuccess, IErrorCallback onError) {
        return new RetrofitCallback<>(merge(of(onSuccess, onError), errorCallback()));
    }

    private static <T> Callback<T> of(ISuccessCallback<T> onSuccess, IErrorCallback onError) {
        return new Callback<>(onSuccess, onError);
    }

    private static <T> Callback<T> errorCallback() {
        return new Callback<>((ignored) -> {}, (error) -> Log.e("Error while api call", error));
    }

    private static <T> Callback<T> merge(Callback<T> left, Callback<T> right) {
        return new Callback<>(
                (data) -> {
                    left.onSuccess.onSuccess(data);
                    right.onSuccess.onSuccess(data);
                },
                (error) -> {
                    left.onError.onError(error);
                    right.onError.onError(error);
                }
        );
    }

    private static final class RetrofitCallback<T> implements retrofit2.Callback<T> {
        private Callback<T> callback;
        private RetrofitCallback(Callback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                callback.onSuccess.onSuccess(response.body());
            } else {
                callback.onError.onError(new ApiException(response));
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            callback.onError.onError(new ApiException(t));
        }
    }
}
