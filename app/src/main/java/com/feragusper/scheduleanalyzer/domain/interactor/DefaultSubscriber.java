package com.feragusper.scheduleanalyzer.domain.interactor;

/**
 * @author Fernando.Perez
 * @since 0.1
 * <p>
 * Default subscriber base class to be used whenever you want default error handling.
 */
public class DefaultSubscriber<T> extends rx.Subscriber<T> {

    @Override
    public void onCompleted() {
        // no-op by default.
    }

    @Override
    public void onError(Throwable e) {
        // no-op by default.
    }

    @Override
    public void onNext(T t) {
        // no-op by default.
    }
}
