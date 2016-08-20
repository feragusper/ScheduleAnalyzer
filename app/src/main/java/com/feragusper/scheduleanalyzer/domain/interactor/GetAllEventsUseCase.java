package com.feragusper.scheduleanalyzer.domain.interactor;

import com.feragusper.scheduleanalyzer.domain.executor.PostExecutionThread;
import com.feragusper.scheduleanalyzer.domain.executor.ThreadExecutor;
import com.feragusper.scheduleanalyzer.domain.repository.EventRepository;

import rx.Observable;

/**
 * Created by Fernando.Perez on 7/8/2016.
 */
public class GetAllEventsUseCase extends UseCase {

    private final long mTimeInMillisFrom;
    private final long mTimeInMillisTo;

    public GetAllEventsUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, long timeInMillisFrom, long timeInMillisTo) {
        super(threadExecutor, postExecutionThread);

        this.mTimeInMillisFrom = timeInMillisFrom;
        this.mTimeInMillisTo = timeInMillisTo;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return new EventRepository().getByDateRange(mTimeInMillisFrom, mTimeInMillisTo);
    }
}
