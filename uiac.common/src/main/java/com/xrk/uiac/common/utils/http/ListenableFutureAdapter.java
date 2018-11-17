package com.xrk.uiac.common.utils.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 类：ListenableFutureAdapter
 * 
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015-4-12
 * <br>==========================
 */
class ListenableFutureAdapter<V> extends ForwardingFuture<V> implements ListenableFuture<V> {

    private static final ThreadFactory threadFactory =
        new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("ListenableFutureAdapter-thread-%d")
            .build();

    private static final Executor defaultAdapterExecutor = Executors.newCachedThreadPool(threadFactory);

    private final Executor adapterExecutor;

    private final ExecutionList executionList = new ExecutionList();

    private final AtomicBoolean hasListeners = new AtomicBoolean(false);

    private final Future<V> delegate;

    ListenableFutureAdapter(Future<V> delegate) {
        this(delegate, defaultAdapterExecutor);
    }

    ListenableFutureAdapter(Future<V> delegate, Executor adapterExecutor) {
        this.delegate = checkNotNull(delegate);
        this.adapterExecutor = checkNotNull(adapterExecutor);
    }

    @Override
    protected Future<V> delegate() {
        return delegate;
    }

    @Override
    public void addListener(Runnable listener, Executor exec) {
        executionList.add(listener, exec);

        if (hasListeners.compareAndSet(false, true)) {
            if (delegate.isDone()) {
                executionList.execute();
                return;
            }

            adapterExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        delegate.get();
                    } catch (Error e) {
                        throw e;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError(e);
                    } catch (Throwable e) {
                    }
                    executionList.execute();
                }
            });
        }
    }

}
