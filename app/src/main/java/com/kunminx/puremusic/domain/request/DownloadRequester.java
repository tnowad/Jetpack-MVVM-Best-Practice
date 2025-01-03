package com.kunminx.puremusic.domain.request;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.kunminx.architecture.domain.dispatch.MviDispatcher;
import com.kunminx.architecture.domain.request.AsyncTask;
import com.kunminx.puremusic.data.bean.DownloadState;
import com.kunminx.puremusic.data.repository.DataRepository;
import com.kunminx.puremusic.domain.event.DownloadEvent;

import io.reactivex.disposables.Disposable;

/**
 * Data Download Request
 * <p>
 * TODO Tip 1: Separate UI from business logic, ensuring that data always flows from producer to consumer.
 * <p>
 * The key difference between UI logic and business logic is that the former is the consumer of data, while the latter is the producer.
 * The "domain layer component" serves as the data producer, and its responsibility should be limited to "request scheduling and result distribution".
 * <p>
 * In other words, the "domain layer component" should only focus on data generation, not on data usage.
 * Logic that modifies the UI state should only be written in the presentation layer, in the Observer callback responding to data changes.
 * When upgrading to Jetpack Compose, this principle remains the same.
 * <p>
 * Example usage:
 * Activity {
 * onCreate(){
 * vm.livedata.observe { result->
 * panel.visible(result.show ? VISIBLE : GONE)
 * tvTitle.setText(result.title)
 * tvContent.setText(result.content)
 * }
 * }
 * <p>
 * For a deeper understanding, refer to the article on "Jetpack MVVM Layered Architecture":
 * https://xiaozhuanlan.com/topic/6741932805
 * <p>
 * Created by KunMinX on 20/03/18
 */
public class DownloadRequester extends MviDispatcher<DownloadEvent> {

    private Disposable mDisposable;

    //TODO Tip 2: Based on the "Single Responsibility Principle", Jetpack's ViewModel framework should be divided into state-ViewModel and result-ViewModel.
    // result-ViewModel serves as a domain layer component, inheriting only the "scope management" ability from the Jetpack ViewModel framework,
    // so that the business instance can be uniquely owned by a single page or shared across multiple pages, e.g.:
    //
    // mDownloadRequester = getFragmentScopeViewModel(DownloadRequester.class);
    // mGlobalDownloadRequester = getActivityScopeViewModel(DownloadRequester.class);
    //
    // In this case, mDownloadRequester for fragment scope only handles DownloadEvent.EVENT_DOWNLOAD business,
    // while mGlobalDownloadRequester for activity scope handles DownloadEvent.EVENT_DOWNLOAD_GLOBAL business.
    // Both are held by the SearchFragment to compare the effects of different scopes.

    @Override
    protected void onHandle(DownloadEvent event) {
        DataRepository repo = DataRepository.getInstance();
        switch (event.eventId) {
            case DownloadEvent.EVENT_DOWNLOAD:
                repo.downloadFile().subscribe(new AsyncTask.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }
                    @Override
                    public void onNext(Integer integer) {
                        sendResult(event.copy(new DownloadState(true, integer)));
                    }
                });
                break;
            case DownloadEvent.EVENT_DOWNLOAD_GLOBAL:
                repo.downloadFile().subscribe((AsyncTask.Observer<Integer>) integer -> {
                    sendResult(event.copy(new DownloadState(true, integer)));
                });
                break;
        }
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        super.onStop(owner);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}

