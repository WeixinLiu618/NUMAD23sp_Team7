package edu.northeastern.numad23sp_team7.huskymarket.utils;

import java.util.HashMap;

public class NotificationUtil {
    public static HashMap<String, String> msg = null;

    public static HashMap<String, String> getMsg() {
        if (msg == null) {
            msg = new HashMap<>();
            msg.put(Constants.MSG_AUTHORIZATION,
                    "key=AAAAt0ALpps:APA91bEpuagOjLQ2j3OjMXCMWmRAbZOpmUAMq2aoulPfxASMpMfWTcVUQu-5IlDm2wvCVKOlRZqGb4QasybMRrjBQ81FOz1HpzsclA0U8bFJ1BCGaD79PVeUIFzSFbfn8EWYtvqnfoWa");
        }
        msg.put(Constants.MSG_CONTENT_TYPE, "application/json");
        return msg;
    }
}
