package com.dk.lib_dk.view.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.R;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.http.NetUtils;
import com.dk.lib_dk.utils.ui.ToastUtil; 
import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.BottomListPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.LoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.net.SocketTimeoutException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BindingActivity<V extends ViewBinding> extends AppCompatActivity {
    protected LoadingDialog loadingDialog;
    protected CompositeDisposable mCompositeDisposable;
    protected TitleBar titleBar;
    protected boolean isShowExitDial = false;

    private V binding;

    public V getBinding() {
        return binding;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ViewBindingUtil.inflateWithGeneric(this, getLayoutInflater());
        setContentView(binding.getRoot());
        setTitleBar(getTitleMsg());
        if (null != titleBar && isShowExitDial) {
            titleBar.getLeftText().setOnClickListener(view -> {
                showExitDialog();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证 Activity 结束时取消所有正在执行的订阅
        }
        mCompositeDisposable = null;
    }

    //region sys

    protected abstract String getTitleMsg();

    protected void setTitleBar(String s) {
        titleBar = findViewById(R.id.titlebar);
        if (null != titleBar) {
            titleBar.setTitle(s);
            titleBar.getLeftText().setOnClickListener(view -> {
                finish();
            });
        }
    }

    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入容器集中处理
    }

    public void killMyself() {
        this.finish();
    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sleepEvent(int time, SuccessBack<Boolean> successBack) {
        handleCompletableEvent(Completable.create(emitter -> {
            sleep(time);
            emitter.onComplete();
        }), data -> {
            if (null != successBack) {
                successBack.getData(true);
            }
        }, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowExitDial) {
                showExitDialog();
                return true;
            } else return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    //endregion

    //region 对话框
    MaterialDialog dialog;
    MaterialDialog tipDialog;

    public void showTipDialog(String info) {
        if (null == tipDialog)
            tipDialog = new MaterialDialog.Builder(BindingActivity.this)
                    .content(info)
                    .title("系统提示")
                    .cancelable(false)
                    .negativeText("确定").build();
        tipDialog.cancel();
        tipDialog.setContent(info);
        tipDialog.show();
    }

    /**
     * @param title
     * @param info
     * @param positiveText 确定的，
     * @param negativeText 不确定
     * @param onPositive
     * @param onNegative
     */
    public void showDialog(String title, String info, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback onPositive, MaterialDialog.SingleButtonCallback onNegative) {
        if (null != dialog) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(BindingActivity.this)
                .content(info)
                .title(title)
                .cancelable(false);
        if (null != positiveText) {
            builder.positiveText(positiveText);
        }
        if (null != negativeText) {
            builder.negativeText(negativeText);
        }
        if (null != onNegative) {
            builder.onNegative((dialog, which) -> {
                if (!canle) {
                    onNegative.onClick(dialog, which);
                }
                canle = true;
            });
        }
        if (null != onPositive) {
            builder.onPositive((dialog, which) -> {
                if (!canle) {
                    onPositive.onClick(dialog, which);
                }
                canle = true;
            });
        }
        dialog = builder.show();
        canle = false;
    }

    volatile boolean canle = false;//防止重复确认，取消

    public void showDialog(String info, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback onPositive, MaterialDialog.SingleButtonCallback onNegative) {
        showDialog("系统提示", info, positiveText, negativeText, onPositive, onNegative);
    }

    public void showDialog(String info, MaterialDialog.SingleButtonCallback onPositive) {
        showDialog(info, "确定", "取消", onPositive, null);
    }

    public void showExitDialog() {
        showDialog("确定返回吗？", (dialog, w) -> {
            killMyself();
        });
    }

    public void showMessage(final String message) {
        if (message == null) {
            return;
        }
        runOnUiThread(() -> {
            ToastUtil.showToast(BindingActivity.this, message, Toast.LENGTH_LONG);
        });
    }
    //endregion

    //region   进度条
    public void updateMessage(String msg) {
        runOnUiThread(() -> {
            if (null == loadingDialog) {
                showLoadingDialog(msg);
                return;
            }
            loadingDialog.updateMessage(msg);
            if (!loadingDialog.isLoading()) {
                loadingDialog.show();
            }
        });
    }

    /**
     * 显示对话框进度
     *
     * @param cxt 上下文
     */
    public void showLoadingDialog(Context cxt, String info) {
        runOnUiThread(() -> {
            if (null == loadingDialog) {
                loadingDialog = WidgetUtils.getLoadingDialog(cxt);
                loadingDialog.setCancelable(true);
            }
            loadingDialog.updateMessage(info);
            if (!loadingDialog.isLoading()) {
                loadingDialog.show();
            }
        });
    }

    /**
     * 显示对话框进度
     */
    public void showLoadingDialog() {
        showLoadingDialog("Loading...");
    }

    public void showLoadingDialog(String info) {
        showLoadingDialog(BindingActivity.this, info);
    }

    /**
     * 取消进度对话框
     */
    public void cancleLoadingDialog() {
        runOnUiThread(() -> {
            if (null != loadingDialog) {
                loadingDialog.cancel();
            }
        });
    }


    public void showLoading() {
        showLoadingDialog();
    }

    public void cancleLoading() {
        cancleLoadingDialog();
    }

    //endregion

    //region 权限

    public void requestPermiss(SuccessBack<Boolean> successBack, String... permissions) {
        XXPermissions.with(this)
                // 申请单个权限
                //.permission(Permission.RECORD_AUDIO)
                // 申请多个权限
                // .permission(permissions)
                .permission(permissions)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            if (null != successBack) {
                                successBack.getData(true);
                            }
                            return;
                        }
                        showMessage("请按提示打开权限");
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(BindingActivity.this, permissions);
                        } else showMessage("请按提示打开权限");
                    }
                });
    }

    public void requestPermisionFile(SuccessBack<Boolean> successBack) {
        requestPermiss(successBack, Permission.MANAGE_EXTERNAL_STORAGE);
    }

    //endregion

    //region 异常处理

    public void handelError(String info, Throwable e) {
        if (null == e) {
            e = new CommonException("");
        }
        cancleLoadingDialog();
        String eInfo = info + e.getMessage() == null ? e.toString() : e.getMessage();
        showMessage(eInfo);
    }

    public void handelError(Throwable e) {
        handelError("", e);
    }


    public void handleBackError(FailedCallback failedCallback, Throwable throwable) {
        handleBackError("", throwable, failedCallback);
    }

    public void handleBackError(String s, Throwable throwable, FailedCallback failedCallback) {
        try {
            if (null != failedCallback) {
                failedCallback.onError(throwable);
            } else {
                cancleLoading();
                String title = ObjectUtils.isNotEmpty(s) ? "[" + s + "]" : "";
                String info = title + throwable.getMessage();
                showTipDialog(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Singe

    public <T> void handleSingeEvent(Single<T> single, SuccessBack<T> suc) {
        handleSingeEvent(single, suc, false);
    }

    public <T> void handleSingeEvent(Single<T> single, SuccessBack<T> suc, boolean isShow) {
        handleSingeEvent(single, suc, null, isShow);
    }

    public <T> void handleSingeEvent(Single<T> single, SuccessBack<T> suc, FailedCallback fail, boolean isShow) {
        handleSingeEvent(single, suc, fail, obtStart(null, isShow), obtAfter(null, isShow));
    }

    public <T> void handleSingeEvent(Single<T> single, SuccessBack<T> suc, FailedCallback fail, Consumer<? super Disposable> start, Action after) {
        Disposable ds = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(start)
                .doAfterTerminate(after)
                .subscribe(
                        (data) -> {
                            if (null != suc)
                                suc.getData(data);
                        }, e -> {
                            handleRxError(fail, e);
                        });
        addDispose(ds);
    }


    protected Consumer<? super Disposable> obtStart(Consumer<? super Disposable> start, boolean isShow) {
        if (null == start) {//自动填充为显示进度
            start = disposable -> {
                if (isShow) showLoadingDialog();
            };
        }
        return start;
    }

    protected Action obtAfter(Action after, boolean isShow) {
        if (null == after) {
            after = () -> {
                if (isShow) cancleLoadingDialog();
            };
        }
        return after;
    }
    //endregion

    //region Completable

    public void handleCompletableEvent(Completable com, SuccessBack suc) {
        handleCompletableEvent(com, suc, false);
    }

    public <T> void handleCompletableEvent(Completable com, SuccessBack<T> suc, boolean isShow) {
        handleCompletableEvent(com, suc, null, isShow);
    }

    public void handleCompletableEvent(Completable com, SuccessBack suc, FailedCallback fail, boolean isShow) {
        handleCompletableEvent(com, suc, fail, obtStart(null, isShow), obtAfter(null, isShow));
    }

    public void handleCompletableEvent(Completable com, SuccessBack suc, FailedCallback fail, Consumer<? super Disposable> start, Action after) {
        Disposable ds = com.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(start)
                .doAfterTerminate(after)
                .subscribe(
                        () -> {
                            if (null != suc)
                                suc.getData("成功");
                        }, e -> {
                            handleRxError(fail, e);
                        });
        addDispose(ds);
    }

    protected void handleRxError(FailedCallback fail, Throwable e) {
        if (null != fail)
            fail.onError(e);
        else {
            if (e instanceof SocketTimeoutException) {
                handelError(new CommonException("连接超时,请检查设备是否连接"));
                return;
            }
            handelError(e);
        }
    }

    //endregion

    //region Observe

    public <T> void handleObserveEvent(Observable<T> obs, SuccessBack<T> suc) {
        handleObserveEvent(obs, suc, false);
    }

    public <T> void handleObserveEvent(Observable<T> obs, SuccessBack<T> succ, boolean isShow) {
        handleObserveEvent(obs, succ, null, isShow);
    }

    public <T> void handleObserveEvent(Observable<T> obs, SuccessBack<T> succ, FailedCallback fail, boolean isShow) {
        handleObserveEvent(obs, succ, fail, obtStart(null, isShow), obtAfter(null, isShow));
    }

    public <T> void handleObserveEvent(Observable<T> obs, SuccessBack<T> succ, FailedCallback fail, Consumer<? super Disposable> start, Action after) {
        obs.subscribeOn(Schedulers.io())
                .doOnSubscribe(start)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(after)
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDispose(d);//防止内存泄漏
                    }

                    @Override
                    public void onNext(T s) {
                        if (null != succ) {
                            succ.getData(s);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleRxError(fail, e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("", "");
                    }
                });
    }

    //endregion

    //region    socket访问网络

    public Single<Boolean> pingIp(String ip) {
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(NetUtils.ping(ip));
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public void pingIp(String ip, SuccessBack<Boolean> successBack) {
        handleSingeEvent(pingIp(ip), data -> {
            if (null != successBack) successBack.getData(data);
        });
    }

    //endregion

    //region 显示list选项
    public void showPicker(List<String> list, String title, OnSelectListener onSelectListener) {
        showPicker(list, title, -1, onSelectListener);
    }

    public void showPicker(List<String> list, String title, int checkedPosition, OnSelectListener onSelectListener) {
        String[] strings = new String[list.size()];
        list.toArray(strings);

        int maxHigh = ScreenUtils.getAppScreenHeight();
        BottomListPopupView popupView = new XPopup.Builder(this)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asBottomList(title, strings, null, checkedPosition, onSelectListener);
        popupView.popupInfo.maxHeight = (int) (maxHigh * 0.60);
        popupView.show();
    }
    //endregion

}
