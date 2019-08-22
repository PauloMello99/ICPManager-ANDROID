package br.com.fatec.icpmanager.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.fatec.icpmanager.R;

public class Helper {

    private static final String REGEX = " ";

    public static boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() < 6;
    }

    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    public static String cutName(@NonNull String name) {
        String[] splittedName = name.split(REGEX);
        return splittedName[0];
    }

    public static String cutName(@NonNull String name, @NonNull int start, @NonNull int end) {
        if (end <= start) return null;
        String[] splittedName = name.split(REGEX);
        StringBuilder resultName = new StringBuilder();
        if (end > splittedName.length)
            for (int i = start; i < splittedName.length; i++) {
                if (i != end - 1)
                    resultName.append(splittedName[i]).append(" ");
                else
                    resultName.append(splittedName[i]);
            }
        else
            for (int i = start; i < end; i++) {
                if (i != end - 1)
                    resultName.append(splittedName[i]).append(" ");
                else
                    resultName.append(splittedName[i]);
            }
        return resultName.toString();
    }

    public static String cutName(@NonNull String name, @NonNull int index) {
        String[] splittedName = name.split(REGEX);
        return splittedName[index];
    }

    public static void openFacebookProfile(String url, Context context){
        try{
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo("com.facebook.katana", 0);
            if(applicationInfo.enabled){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+url));
                context.startActivity(intent);
            } else {
                Log.e("FACEBOOK_INTENT_ERR","FACEBOOK NOT INSTALLED");
                Toast.makeText(context, context.getString(R.string.failure_open_facebook), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Log.e("FACEBOOK_INTENT_ERR",e.getMessage());
            Toast.makeText(context, context.getString(R.string.failure_open_facebook), Toast.LENGTH_SHORT).show();
        }
    }

    public static void openSkypeProfile(String url, Context context){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("skype:"+url));
            context.startActivity(intent);
        } catch (Exception e){
            Log.e("FACEBOOK_INTENT_ERR",e.getMessage());
            Toast.makeText(context, context.getString(R.string.failure_open_skype), Toast.LENGTH_SHORT).show();
        }
    }

}