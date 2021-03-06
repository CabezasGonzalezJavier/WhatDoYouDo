package com.thedeveloperworldisyours.whatdoyoudoandroid.webservice;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.thedeveloperworldisyours.whatdoyoudoandroid.models.Mission;
import com.thedeveloperworldisyours.whatdoyoudoandroid.models.Node;
import com.thedeveloperworldisyours.whatdoyoudoandroid.utils.Constants;
import com.thedeveloperworldisyours.whatdoyoudoandroid.utils.Utils;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by javiergonzalezcabezas on 19/4/15.
 */
public class Client {
    public interface ClientInterface{
    @GET("/missions/")
    void getMissions(@Query("language") String languaje, Callback<List<Mission>> callback);
    @GET("/nodes/")
        void getNodes(@Query("mission") String mission,Callback<List<Node>> callback);
}

    public static ClientInterface initRestAdapter()
    {
        OkHttpClient client = new OkHttpClient();

        return (ClientInterface) new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(Constants.URL)
                .build()
                .create(ClientInterface.class);
    }


}
