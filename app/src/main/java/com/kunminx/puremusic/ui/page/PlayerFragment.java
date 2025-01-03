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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kunminx.architecture.ui.page.BaseFragment;
import com.kunminx.architecture.ui.page.DataBindingConfig;
import com.kunminx.architecture.ui.page.StateHolder;
import com.kunminx.architecture.ui.state.State;
import com.kunminx.architecture.utils.Res;
import com.kunminx.architecture.utils.ToastUtils;
import com.kunminx.architecture.utils.Utils;
import com.kunminx.player.domain.PlayingInfoManager;
import com.kunminx.puremusic.BR;
import com.kunminx.puremusic.R;
import com.kunminx.puremusic.databinding.FragmentPlayerBinding;
import com.kunminx.puremusic.domain.event.Messages;
import com.kunminx.puremusic.domain.message.DrawerCoordinateManager;
import com.kunminx.puremusic.domain.message.PageMessenger;
import com.kunminx.puremusic.domain.proxy.PlayerManager;
import com.kunminx.puremusic.ui.page.helper.DefaultInterface;
import com.kunminx.puremusic.ui.view.PlayerSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by KunMinX on 19/10/29
 */
public class PlayerFragment extends BaseFragment {

    // TODO tip 1: Based on the "Single Responsibility Principle", the ViewModel should be divided into state-ViewModel and result-ViewModel,
    // the state-ViewModel should only be responsible for managing, saving, and restoring the state of this page, and its scope should be limited to this page,
    // while the result-ViewModel should be responsible for "message dispatching" in "trusted sources" scenarios, with its scope depending on "data requests" or "cross-page communication" message dispatching range.

    private PlayerStates mStates;
    private PlayerSlideListener.SlideAnimatorStates mAnimatorStates;
    private PageMessenger mMessenger;
    private PlayerSlideListener mListener;

    @Override
    protected void initViewModel() {
        mStates = getFragmentScopeViewModel(PlayerStates.class);
        mAnimatorStates = getFragmentScopeViewModel(PlayerSlideListener.SlideAnimatorStates.class);
        mMessenger = getApplicationScopeViewModel(PageMessenger.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {

        // TODO tip 2: Strict DataBinding mode:
        // The DataBinding instance is limited to the base page, not exposed to subclasses.
        // This resolves the view instance null safety consistency issue.
        // By doing this, the view instance's null safety will match Jetpack Compose's functional programming ideas.

        return new DataBindingConfig(R.layout.fragment_player, BR.vm, mStates)
            .addBindingParam(BR.panelVm, mAnimatorStates)
            .addBindingParam(BR.click, new ClickProxy())
            .addBindingParam(BR.listener, new ListenerHandler());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO tip 3: Demonstrates using the "trusted source" MVI-Dispatcher input-output interface for message sending and receiving

        mMessenger.output(this, messages -> {
            switch (messages.eventId) {
                case Messages.EVENT_ADD_SLIDE_LISTENER:
                    if (view.getParent().getParent() instanceof SlidingUpPanelLayout) {
                        SlidingUpPanelLayout sliding = (SlidingUpPanelLayout) view.getParent().getParent();

                        // TODO tip 4: Avoid getting the binding instance or view instance in subclasses unless necessary.
                        // The current solution is to provide a debug mode to give warnings for instance acquisition.

                        mListener = new PlayerSlideListener((FragmentPlayerBinding) getBinding(), mAnimatorStates, sliding);
                        sliding.addPanelSlideListener(mListener);
                        sliding.addPanelSlideListener(new DefaultInterface.PanelSlideListener() {
                            @Override
                            public void onPanelStateChanged(
                                View view, SlidingUpPanelLayout.PanelState panelState,
                                SlidingUpPanelLayout.PanelState panelState1) {
                                DrawerCoordinateManager.getInstance().requestToUpdateDrawerMode(
                                    panelState1 == SlidingUpPanelLayout.PanelState.EXPANDED,
                                    this.getClass().getSimpleName()
                                );
                            }
                        });
                    }
                    break;
                case Messages.EVENT_CLOSE_SLIDE_PANEL_IF_EXPANDED:
                    // If the slide panel is expanded, slide it down on back button press.

                    if (view.getParent().getParent() instanceof SlidingUpPanelLayout) {
                        SlidingUpPanelLayout sliding = (SlidingUpPanelLayout) view.getParent().getParent();
                        if (sliding.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                            sliding.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        } else {
                            mMessenger.input(new Messages(Messages.EVENT_CLOSE_ACTIVITY_IF_ALLOWED));
                        }
                    } else {
                        mMessenger.input(new Messages(Messages.EVENT_CLOSE_ACTIVITY_IF_ALLOWED));
                    }
                    break;
            }
        });

        // TODO tip 6: All playback state changes come from the unified distribution via getUiStates(),
        // ensuring "message dispatch reliability and consistency" to avoid unexpected pushes and errors.

        PlayerManager.getInstance().getUiStates().observe(getViewLifecycleOwner(), uiStates -> {
            mStates.musicId.set(uiStates.getMusicId(), changed -> {
                mStates.title.set(uiStates.getTitle());
                mStates.artist.set(uiStates.getSummary());
                mStates.coverImg.set(uiStates.getImg());
                if (mListener != null) view.post(mListener::calculateTitleAndArtist);
                mStates.maxSeekDuration.set(uiStates.getDuration());
            });
            mStates.currentSeekPosition.set(uiStates.getProgress());
            mStates.isPlaying.set(!uiStates.isPaused());
            mStates.repeatMode.set(uiStates.getRepeatMode(), changed -> {
                mStates.playModeIcon.set(PlayerManager.getInstance().getModeIcon(uiStates.getRepeatMode()));
            });
        });
    }

    // TODO tip 7: Use DataBinding to avoid the null safety consistency issues when setting onClickListener.

    public class ClickProxy {

        public void playMode() {
            PlayerManager.getInstance().changeMode();
        }

        public void previous() {
            PlayerManager.getInstance().playPrevious();
        }

        public void togglePlay() {
            PlayerManager.getInstance().togglePlay();
        }

        public void next() {
            PlayerManager.getInstance().playNext();
        }

        public void showPlayList() {
            ToastUtils.showShortToast(getString(R.string.unfinished));
        }

        public void slideDown() {
            mMessenger.input(new Messages(Messages.EVENT_CLOSE_SLIDE_PANEL_IF_EXPANDED));
        }

        public void more() {
        }
    }

    public static class ListenerHandler implements DefaultInterface.OnSeekBarChangeListener {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PlayerManager.getInstance().setSeek(seekBar.getProgress());
        }
    }

    // TODO tip 8: Based on the Single Responsibility Principle, extract Jetpack ViewModel "state saving and restoring" abilities into StateHolder,
    // and use the improved ObservableField subclass State as BehaviorSubject to serve as the "trusted data source" for bound controls,
    // so that when the result from PublishSubject is received, the control properties are notified to re-render, holding the last state.

    public static class PlayerStates extends StateHolder {
        public final State<String> musicId = new State<>("", true);
        public final State<Enum<PlayingInfoManager.RepeatMode>> repeatMode = new State<>(PlayingInfoManager.RepeatMode.LIST_CYCLE, true);
        public final State<String> title = new State<>(Utils.getApp().getString(R.string.app_name), true);
        public final State<String> artist = new State<>(Utils.getApp().getString(R.string.app_name), true);
        public final State<String> coverImg = new State<>("", true);
        public final State<Drawable> placeHolder = new State<>(Res.getDrawable(R.drawable.bg_album_default), true);
        public final State<Integer> maxSeekDuration = new State<>(0, true);
        public final State<Integer> currentSeekPosition = new State<>(0, true);
        public final State<Boolean> isPlaying = new State<>(false, true);
        public final State<MaterialDrawableBuilder.IconValue> playModeIcon = new State<>(PlayerManager.getInstance().getModeIcon(), true);
    }

}
