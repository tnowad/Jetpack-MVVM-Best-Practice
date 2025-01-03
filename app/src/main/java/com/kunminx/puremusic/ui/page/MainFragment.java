/*
 * Copyright 2018-present KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kunminx.puremusic.ui.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kunminx.architecture.ui.page.BaseFragment;
import com.kunminx.architecture.ui.page.DataBindingConfig;
import com.kunminx.architecture.ui.page.StateHolder;
import com.kunminx.architecture.ui.state.State;
import com.kunminx.puremusic.BR;
import com.kunminx.puremusic.R;
import com.kunminx.puremusic.data.bean.TestAlbum;
import com.kunminx.puremusic.domain.event.Messages;
import com.kunminx.puremusic.domain.message.PageMessenger;
import com.kunminx.puremusic.domain.proxy.PlayerManager;
import com.kunminx.puremusic.domain.request.MusicRequester;
import com.kunminx.puremusic.ui.page.adapter.PlaylistAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KunMinX at 19/10/29
 */
public class MainFragment extends BaseFragment {

    //TODO tip 1: Based on the "Single Responsibility Principle," ViewModel should be divided into state-ViewModel and result-ViewModel.
    // The state-ViewModel should only be responsible for managing, saving, and restoring the page's state, with its scope limited to the page itself.
    // The result-ViewModel should only be responsible for "message dispatch" scenarios and should manage "trusted sources" depending on the scope of "data requests" or "cross-page communication."

    // For more details, refer to: https://xiaozhuanlan.com/topic/8204519736

    private MainStates mStates;
    private PageMessenger mMessenger;
    private MusicRequester mMusicRequester;
    private PlaylistAdapter mAdapter;

    @Override
    protected void initViewModel() {
        mStates = getFragmentScopeViewModel(MainStates.class);
        mMessenger = getApplicationScopeViewModel(PageMessenger.class);
        mMusicRequester = getFragmentScopeViewModel(MusicRequester.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {

        mAdapter = new PlaylistAdapter(getContext());
        mAdapter.setOnItemClickListener((viewId, item, position) -> {
            PlayerManager.getInstance().playAudio(position);
        });

        //TODO tip 2: Strict mode for DataBinding:
        // Limit the DataBinding instance to the base page, making it not exposed to subclasses by default.
        // This approach resolves the issue of ensuring null safety for View instances, achieving consistent null safety between View instances and Jetpack Compose, based on functional programming principles.
        // DataBindingConfig provides the binding items for the base page's DataBinding in this context.

        // For more details, refer to: https://xiaozhuanlan.com/topic/9816742350 and https://xiaozhuanlan.com/topic/2356748910

        return new DataBindingConfig(R.layout.fragment_main, BR.vm, mStates)
            .addBindingParam(BR.click, new ClickProxy())
            .addBindingParam(BR.adapter, mAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO tip 3: All changes in play state should come from the "trusted source" PlayerManager for consistent message distribution,
        // ensuring "reliable and consistent message dispatch" and avoiding unexpected pushes or errors.

        // For more details, refer to: https://xiaozhuanlan.com/topic/6017825943 & https://juejin.cn/post/7117498113983512589

        PlayerManager.getInstance().getUiStates().observe(getViewLifecycleOwner(), uiStates -> {
            mStates.musicId.set(uiStates.getMusicId(), changed -> mAdapter.notifyDataSetChanged());
        });

        //TODO tip 4:
        // The `getViewLifeCycleOwner` method was introduced in 2020 to solve the lifecycle safety problem when `getView()` is null.
        // In a fragment, use `getViewLifecycleOwner` as the observer for liveData.
        // No changes are needed for Activity.

        mMusicRequester.getFreeMusicsResult().observe(getViewLifecycleOwner(), dataResult -> {
            if (!dataResult.getResponseStatus().isSuccess()) return;

            TestAlbum musicAlbum = dataResult.getResult();

            // TODO tip 5: LiveData without UnPeek handling will automatically push data during view controller recreation.
            // This must be handled properly to avoid unexpected issues such as receiving old data.

            // For more details, refer to: https://xiaozhuanlan.com/topic/6719328450

            if (musicAlbum != null && musicAlbum.musics != null) {
                mStates.list.set(musicAlbum.musics);
                PlayerManager.getInstance().loadAlbum(musicAlbum);
            }
        });

        mMessenger.output(this, messages -> {
            switch (messages.eventId) {
                case Messages.EVENT_LOGIN_SUCCESS:
                    //TODO tip:
                    // Handle post-login actions, such as refreshing page state after loginFragment succeeds.
                    break;
            }
        });

        if (PlayerManager.getInstance().getAlbum() == null) mMusicRequester.requestFreeMusics();
    }

    // TODO tip 7: This approach uses DataBinding to avoid issues with view instance null safety consistency when setting onClickListener.

    // Essentially, binding occurs when a view exists, and if no view is available, binding doesn't happen, ensuring no null safety issues.
    // For more details, refer to: https://xiaozhuanlan.com/topic/9816742350

    public class ClickProxy {

        public void openMenu() {

            // TODO tip 8: Here, the request is sent to a "trusted source" to ensure "lifecycle-safe, reliable and consistent message dispatch."

            // For more details, refer to: https://xiaozhuanlan.com/topic/6017825943 & https://juejin.cn/post/7117498113983512589
            // --------
            // This also demonstrates the "least knowledge principle," where internal activities are handled inside the Activity,
            // rather than manipulating Activity internals from the fragment.
            // This ensures the Activity's future changes are applied more generally to other fragments, not just the current one.

            mMessenger.input(new Messages(Messages.EVENT_OPEN_DRAWER));
        }

        public void login() {
            nav().navigate(R.id.action_mainFragment_to_loginFragment);
        }

        public void search() {
            nav().navigate(R.id.action_mainFragment_to_searchFragment);
        }

    }

    //TODO tip 9: Each page should prepare a separate state-ViewModel that manages the state bound to the "view properties."
    // State-ViewModel should only be responsible for managing and restoring state, and UI logic should be handled elsewhere (e.g., in Activity/Fragment).

    // The distinction between UI logic and business logic: the former consumes data, while the latter produces data.
    // Data always comes from the domain layer and is pushed back to the UI layer to trigger UI logic.
    // In the future, when upgrading to Jetpack Compose, this distinction becomes even more important.

    // For more details, refer to: https://xiaozhuanlan.com/topic/6741932805

    public static class MainStates extends StateHolder {

        //TODO tip 10: Here, we use a subclass of ObservableField, "State," to replace MutableLiveData, removing the debounce feature.

        // For more details, refer to: https://xiaozhuanlan.com/topic/9816742350

        public final State<String> musicId = new State<>("", true);
        public final State<Boolean> initTabAndPage = new State<>(true);

        public final State<String> pageAssetPath = new State<>("summary.html");

        public final State<List<TestAlbum.TestMusic>> list = new State<>(new ArrayList<>());

    }

}

