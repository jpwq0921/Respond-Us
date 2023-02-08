package com.sp.respond_us;


import android.content.Context;
import android.os.StrictMode;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "AAAA0yxQUhU:APA91bEecnLPXlSgalQ-cFrbxu5d4Tczp2kWGRbxMVE53nFvHqewvRppwgbh5mKpWEDdEHjJVWZROClW90xF2M0shkac89JvxbrpZyEzZ_xsmQYTt-jVcRJWG2ZiDvbf9RdjXJHeuDZA";

    private void sendNotification(List<String> fcmTokens) {
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", "My Notification");
            notification.put("body", "Hello World!");
        } catch (JSONException e) {
            System.out.println("onCreate: " + e);
        }

        JSONObject data = new JSONObject();
        try {
            data.put("click_action", "FLUTTER_NOTIFICATION_CLICK");
            data.put("id", "1");
            data.put("status", "done");
        } catch (JSONException e) {
            System.out.println("onCreate: " + e);
        }

    }
}
