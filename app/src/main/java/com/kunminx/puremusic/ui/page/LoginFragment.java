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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kunminx.architecture.data.config.utils.KeyValueProvider;
import com.kunminx.architecture.ui.page.BaseFragment;
import com.kunminx.architecture.ui.page.DataBindingConfig;
import com.kunminx.architecture.ui.page.StateHolder;
import com.kunminx.architecture.ui.state.State;
import com.kunminx.architecture.utils.ToastUtils;
import com.kunminx.puremusic.BR;
import com.kunminx.puremusic.R;
import com.kunminx.puremusic.data.bean.User;
import com.kunminx.puremusic.data.config.Configs;
import com.kunminx.puremusic.domain.event.Messages;
import com.kunminx.puremusic.domain.message.DrawerCoordinateManager;
import com.kunminx.puremusic.domain.message.PageMessenger;
import com.kunminx.puremusic.domain.request.AccountRequester;

/**
 * Created by KunMinX at 20/04/26
 */
public class LoginFragment extends BaseFragment {

    //TODO tip 1: Based on the "Single Responsibility Principle," ViewModel should be divided into state-ViewModel and result-ViewModel.
    // The state-ViewModel should only be responsible for managing, saving, and restoring the page's state, with its scope limited to the page itself.
    // The result-ViewModel should be responsible only for "message dispatch" and handling "trusted sources" with a scope defined by "data requests" or "cross-page communication" scenarios.
    // For a deeper understanding, refer to: https://xiaozhuanlan.com/topic/8204519736

    private LoginStates mStates;
    private AccountRequester mAccountRequester;
    private PageMessenger mMessenger;
    private final Configs mConfigs = KeyValueProvider.get(Configs.class);

    @Override
    protected void initViewModel() {
        mStates = getFragmentScopeViewModel(LoginStates.class);
        mMessenger = getApplicationScopeViewModel(PageMessenger.class);
        mAccountRequester = getFragmentScopeViewModel(AccountRequester.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {

        //TODO tip 2: Strict mode for DataBinding:
        // Limit the DataBinding instance to the base page, making it not exposed to subclasses by default.
        // This approach resolves the issue of ensuring null safety for View instances, achieving consistent null safety between View instances and Jetpack Compose, based on functional programming principles.
        // DataBindingConfig provides the binding items for the base page's DataBinding in this context.

        // For more details, refer to: https://xiaozhuanlan.com/topic/9816742350 and https://xiaozhuanlan.com/topic/2356748910

        return new DataBindingConfig(R.layout.fragment_login, BR.vm, mStates)
            .addBindingParam(BR.click, new ClickProxy());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLifecycle().addObserver(DrawerCoordinateManager.getInstance());

        //TODO tip 3: Make the accountRequest observable of the page's lifecycle.
        // So, if the page is about to exit and the login request has not finished due to network delay,
        // it can notify the data layer to cancel the request in time to avoid resource waste and unexpected issues.

        getLifecycle().addObserver(mAccountRequester);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO tip 4: Obtain read-only data from the trusted Requester using immutable Result, and set it to the mutable State.
        // Instead of directly setting Result on the page, separate Result and State.

        // For further understanding, refer to the explanation on "Master LiveData's Essence and Enjoy Reliable Message Authentication Mechanism."
        // https://xiaozhuanlan.com/topic/6017825943

        mAccountRequester.getTokenResult().observe(getViewLifecycleOwner(), dataResult -> {
            if (!dataResult.getResponseStatus().isSuccess()) {
                mStates.loadingVisible.set(false);
                ToastUtils.showLongToast(getString(R.string.network_state_retry));
                return;
            }

            String s = dataResult.getResult();
            if (TextUtils.isEmpty(s)) return;

            //TODO tip: After successfully obtaining the token, store it using the KeyValueX framework.
            // Also, notify other pages to refresh their state using the Application-scoped PageMessenger framework.
            // For more details, refer to Configs and PageMessenger classes.

            mConfigs.token().set(s);
            mStates.loadingVisible.set(false);
            mMessenger.input(new Messages(Messages.EVENT_LOGIN_SUCCESS));
            nav().navigateUp();
        });
    }

    public class ClickProxy {

        public void back() {
            nav().navigateUp();
        }

        public void login() {

            //TODO tip 5: Enable two-way data binding to obtain data bound to XML controls from the state-ViewModel's observable data.
            // This avoids directly interacting with control instances and prevents potential null safety consistency issues.

            // For further understanding, refer to: https://xiaozhuanlan.com/topic/9816742350

            if (TextUtils.isEmpty(mStates.name.get()) || TextUtils.isEmpty(mStates.password.get())) {
                ToastUtils.showLongToast(getString(R.string.username_or_pwd_incomplete));
                return;
            }
            User user = new User(mStates.name.get(), mStates.password.get());
            mAccountRequester.requestLogin(user);
            mStates.loadingVisible.set(true);
        }
    }

    //TODO tip 6: Based on the Single Responsibility Principle, extract the ability of Jetpack ViewModel to "save and restore state" as StateHolder.
    // Use a subclass of ObservableField, called State, to act as a BehaviorSubject, serving as the "trusted data source" for bound UI components.
    // This allows UI components to re-render their properties upon receiving data pushed back from PublishSubject and maintain the last state.

    // For more details, refer to: https://xiaozhuanlan.com/topic/6741932805

    public static class LoginStates extends StateHolder {

        public final State<String> name = new State<>("");

        public final State<String> password = new State<>("");

        public final State<Boolean> loadingVisible = new State<>(false);

    }

}
