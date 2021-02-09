package zgas.client.providers;


import zgas.client.models.FCMBody;
import zgas.client.models.FCMResponse;
import zgas.client.retrofit.IFCMApi;
import zgas.client.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
