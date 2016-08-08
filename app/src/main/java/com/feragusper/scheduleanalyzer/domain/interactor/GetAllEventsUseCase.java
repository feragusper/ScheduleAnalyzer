package com.feragusper.scheduleanalyzer.domain.interactor;

import com.feragusper.scheduleanalyzer.domain.executor.PostExecutionThread;
import com.feragusper.scheduleanalyzer.domain.executor.ThreadExecutor;
import com.feragusper.scheduleanalyzer.domain.repository.EventRepository;

import rx.Observable;

/**
 * Created by Fernando.Perez on 7/8/2016.
 */
public class GetAllEventsUseCase extends UseCase {

    public GetAllEventsUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return new EventRepository().getAll();
    }
}
