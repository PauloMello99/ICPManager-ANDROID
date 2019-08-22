package br.com.fatec.icpmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceHelper {

	private SharedPreferences app_prefs;
	private String NIGHT_MODE = "night_mode";
	private String MESSAGE_TOKEN = "message_token";
	private String USER_TYPE = "user_type";
	private String NOTIFICATION = "notification";

	public PreferenceHelper(Context context) {
		app_prefs = context.getSharedPreferences("br.com.fatec.activity.icpmanager", Context.MODE_PRIVATE);
	}

	public void putNightMode(boolean enabled) {
		Editor edit = app_prefs.edit();
		edit.putString(NIGHT_MODE, (enabled) ? "true" : "false");
		edit.apply();
	}

	public String getNightMode() {
		return app_prefs.getString(NIGHT_MODE, "false");
	}

	public void putMessagingToken(String token) {
		Editor edit = app_prefs.edit();
		edit.putString(MESSAGE_TOKEN, token);
		edit.apply();
	}

	public String getMessagingToken() {
		return app_prefs.getString(MESSAGE_TOKEN, null);
	}

	public void putUserType(int type) {
		Editor edit = app_prefs.edit();
		edit.putString(USER_TYPE, String.valueOf(type));
		edit.apply();
	}

	public String getUserType() {
		return app_prefs.getString(USER_TYPE, null);
	}

	public void putNotification(boolean notifications) {
		Editor edit = app_prefs.edit();
		edit.putString(NOTIFICATION, String.valueOf(notifications));
		edit.apply();
	}

    public String getNotification() {
		return app_prefs.getString(NOTIFICATION, null);
    }
}