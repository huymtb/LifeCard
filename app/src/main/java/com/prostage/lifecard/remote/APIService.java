package com.prostage.lifecard.remote;

import com.prostage.lifecard.Model.Post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by HuyMTB on 3/20/2017.
 *
 */

public interface APIService {
    @POST("/login.json")
    Call<Post> savePost(@Body Post post);
}
