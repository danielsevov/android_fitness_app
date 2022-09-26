package com.personal.lifttoliveapp.misc;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.personal.lifttoliveapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static String MAP_NOTIFICATION_CHANNEL = "MNC";
    public static String BACKGROUND_NOTIFICATION_CHANNEL = "BNC";
    public static int reqCode = 1;

    public static void wipeFile() {
        try {
            PrintWriter writer = new PrintWriter("config.txt");
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static String capitalize(String source) {
        StringBuffer res = new StringBuffer();

        String[] strArr = source.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

        return res.toString().trim();
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public static ArrayList<Integer> extractNumbersFromString(String s) {
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(s);
        ArrayList<Integer> numbers = new ArrayList<>();
        while (m.find()) {
            numbers.add(Integer.parseInt(m.group()));
        }
        return numbers;
    }

    public static void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Exception", "Can not read file: " + e.toString());
        }

        return ret;
    }

    /**
     *
     * @param context
     * @param title  --> title to show
     * @param message --> details to show
     * @param intent --> What should happen on clicking the notification
     */
    public static NotificationCompat.Builder createNotification(Context context, String title, String message, Intent intent) {
        PendingIntent pendingIntent = null;
        if(intent != null) pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);

        String CHANNEL_ID = MAP_NOTIFICATION_CHANNEL;// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        if(pendingIntent != null) notificationBuilder.setContentIntent(pendingIntent);


        return notificationBuilder;
    }

    public static void showNotification(Context context, String title, String message, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = MAP_NOTIFICATION_CHANNEL;// The id of the channel.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Vista";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(reqCode, createNotification(context, title, message, intent).build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }
    public static void showNotification(Context context, NotificationCompat.Builder notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = MAP_NOTIFICATION_CHANNEL;// The id of the channel.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Vista";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(reqCode, notification.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

//    //used to change the current fragment to a new fragment via the fragment manager
//    public static void replaceFragment(FragmentManager manager, Fragment frag) {
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.nav_host_fragment_activity_hub, frag);
//        transaction.setReorderingAllowed(true);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

//    //used to change the current fragment to a new fragment via the fragment manager
//    public static void replaceFragmentWithDelay(FragmentManager manager, Fragment frag, long delay) {
//        try {
//            Thread.sleep(delay);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.nav_host_fragment_activity_hub, frag);
//        transaction.setReorderingAllowed(true);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    public static void createToast(String keyValue, Activity activity, Context context) {
        Looper.prepare();
        Toast.makeText(activity.getApplicationContext(), getResourceByName(keyValue, activity, context), Toast.LENGTH_LONG).show();
    }

    public static String getResourceByName(String name, Activity activity, Context context) {
        try {
            int resID = context.getResources().getIdentifier(name, "string", activity.getPackageName());
            return context.getString(resID);
        } catch (Exception ex) {
            return "Error: " + name;
        }
    }

}
