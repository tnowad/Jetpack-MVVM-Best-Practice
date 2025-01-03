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

package com.kunminx.puremusic.data.repository;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunminx.architecture.data.response.DataResult;
import com.kunminx.architecture.data.response.ResponseStatus;
import com.kunminx.architecture.data.response.ResultSource;
import com.kunminx.architecture.domain.request.AsyncTask;
import com.kunminx.architecture.utils.Utils;
import com.kunminx.puremusic.R;
import com.kunminx.puremusic.data.api.APIs;
import com.kunminx.puremusic.data.api.AccountService;
import com.kunminx.puremusic.data.bean.LibraryInfo;
import com.kunminx.puremusic.data.bean.TestAlbum;
import com.kunminx.puremusic.data.bean.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create by KunMinX at 19/10/29
 */
public class DataRepository {

    private static final DataRepository S_REQUEST_MANAGER = new DataRepository();

    private DataRepository() {
    }

    public static DataRepository getInstance() {
        return S_REQUEST_MANAGER;
    }

    private final Retrofit retrofit;

    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build();
        retrofit = new Retrofit.Builder()
            .baseUrl(APIs.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    //TODO Tip: Push data back to the domain layer via the "reactive framework",
    // Correspondingly, in Kotlin, use flow{ ... emit(...) }.flowOn(Dispatchers.xx)

    public Observable<DataResult<TestAlbum>> getFreeMusic() {
        return AsyncTask.doIO(emitter -> {
            Gson gson = new Gson();
            Type type = new TypeToken<TestAlbum>() {
            }.getType();
            TestAlbum testAlbum = gson.fromJson(Utils.getApp().getString(R.string.free_music_json), type);
            emitter.onNext(new DataResult<>(testAlbum, new ResponseStatus()));
        });
    }

    public Observable<DataResult<List<LibraryInfo>>> getLibraryInfo() {
        return AsyncTask.doIO(emitter -> {
            Gson gson = new Gson();
            Type type = new TypeToken<List<LibraryInfo>>() {
            }.getType();
            List<LibraryInfo> list = gson.fromJson(Utils.getApp().getString(R.string.library_json), type);
            emitter.onNext(new DataResult<>(list, new ResponseStatus()));
        });
    }

    /**
     * TODO: Simulate download task:
     */
    @SuppressLint("CheckResult")
    public Observable<Integer> downloadFile() {
        return AsyncTask.doIO(emitter -> {
            // Simulate "data read and write" in memory, pretending to be "file IO",

            byte[] bytes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int b;
                while ((b = bis.read()) != -1) {
                    Thread.sleep(500);
                    emitter.onNext(b);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * TODO: Simulate network request for login
     *
     * @param user User information entered by the UI layer
     */
    public Observable<DataResult<String>> login(User user) {

        // Use retrofit or any library you prefer for network requests. Here's a simple example using retrofit,
        // If you use RxJava, you can additionally depend on RxJavaCallAdapterFactory to simplify the code.
        // Refer to online resources for further details, no elaboration here.

        return AsyncTask.doIO(emitter -> {
            Call<String> call = retrofit.create(AccountService.class).login(user.getName(), user.getPassword());
            Response<String> response;
            try {
                response = call.execute();
                ResponseStatus responseStatus = new ResponseStatus(
                    String.valueOf(response.code()), response.isSuccessful(), ResultSource.NETWORK);
                emitter.onNext(new DataResult<>(response.body(), responseStatus));
            } catch (IOException e) {
                emitter.onNext(new DataResult<>(null,
                    new ResponseStatus(e.getMessage(), false, ResultSource.NETWORK)));
            }
        });
    }
}
