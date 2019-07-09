package com.example.sbandroidsampleapptutorial.utils;

import android.content.Context;
import android.content.SharedPreferences;


/*
 * This is where you store certain options persistently through out
 */
public class PreferenceUtils {

    private static final String PREFERENCE_KEY_USER_ID = "userId";
    private static final String PREFERENCE_KEY_NICKNAME = "nickname";
    private static final String PREFERENCE_KEY_CONNECTED = "connected";

    private static final String PREFERENCE_KEY_NOTIFICATIONS = "notifications";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_SHOW_PREVIEWS = "notificationsShowPreviews";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB = "notificationsDoNotDisturb";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_FROM = "notificationsDoNotDisturbFrom";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_TO = "notificationsDoNotDisturbTo";
    private static final String PREFERENCE_KEY_GROUP_CHANNEL_DISTINCT = "channelDistinct";
    private static Context mAppContext;

    private PreferenceUtils() {
    }

    public static void init(Context appContext) {
        mAppContext = appContext;
    }


    private static SharedPreferences getSharedPreferences () {
        SharedPreferences mSharedPreference = mAppContext.getSharedPreferences("senddbird", Context.MODE_PRIVATE);
        return mSharedPreference;
    }

    // this will get us the global UserId throughout the app
    public static String getUserId(){
        String mUserId = getSharedPreferences().getString(PREFERENCE_KEY_USER_ID, "");
        return mUserId;
    }

    public static void setUserId(String userId){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_USER_ID, userId).apply();
    }

    // this will get us the nickname throughout the app
    public static String getNickname(){
        String mNickname = getSharedPreferences().getString(PREFERENCE_KEY_NICKNAME, "");
        return mNickname;
    }

    public static void setNickname(String nickname){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_NICKNAME, nickname).apply();
    }


    // getter/setter for connection
    public static boolean getConnected(boolean tf){
        Boolean bool = getSharedPreferences().getBoolean(PREFERENCE_KEY_CONNECTED, false);
        return bool;
    }

    public static void setConnected(boolean tf){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_CONNECTED, tf).apply();
    }








    public static void clearAll() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear().apply();
    }

    public static void setNotifications(boolean notifications) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_NOTIFICATIONS, notifications).apply();
    }

    public static boolean getNotifications() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_NOTIFICATIONS, true);
    }

    public static void setNotificationsShowPreviews(boolean notificationsShowPreviews) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_NOTIFICATIONS_SHOW_PREVIEWS, notificationsShowPreviews).apply();
    }

    public static boolean getNotificationsShowPreviews() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_NOTIFICATIONS_SHOW_PREVIEWS, true);
    }

    public static void setNotificationsDoNotDisturb(boolean notificationsDoNotDisturb) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB, notificationsDoNotDisturb).apply();
    }

    public static boolean getNotificationsDoNotDisturb() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB, false);
    }

    public static void setNotificationsDoNotDisturbFrom(String notificationsDoNotDisturbFrom) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_FROM, notificationsDoNotDisturbFrom).apply();
    }

    public static String getNotificationsDoNotDisturbFrom() {
        return getSharedPreferences().getString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_FROM, "");
    }

    public static void setNotificationsDoNotDisturbTo(String notificationsDoNotDisturbTo) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_TO, notificationsDoNotDisturbTo).apply();
    }

    public static String getNotificationsDoNotDisturbTo() {
        return getSharedPreferences().getString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_TO, "");
    }
    public static void setGroupChannelDistinct(boolean channelDistinct) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_GROUP_CHANNEL_DISTINCT, channelDistinct).apply();
    }

    public static boolean getGroupChannelDistinct() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_GROUP_CHANNEL_DISTINCT, true);
    }
}
