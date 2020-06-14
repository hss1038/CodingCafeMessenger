package com.ssam.codingcafemessenger.Fragments;

import com.ssam.codingcafemessenger.Notifications.MyResponse;
import com.ssam.codingcafemessenger.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
        @Headers({
                "Content-Type:application/json",
                "Authorization:key=AAAAjGwd8AQ:APA91bHmAkwudl-QpWwxc-WbVeLOhzYg1W_EE4X7fubXcI2Zj69Mk9O7DRYCIZgayFs6iUw5pg5NoAcJtROclFfiQw9k7-FIn8xNEBEGCdQ5odG53p2QzOzZXT-egis-xjfbRI0ONrju"
        })

        @POST("fcm/send")
        Call<MyResponse> sendNotification(@Body Sender body);
}
