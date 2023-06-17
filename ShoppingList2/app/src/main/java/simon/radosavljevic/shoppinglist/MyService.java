package simon.radosavljevic.shoppinglist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
public class MyService extends Service {
    private DbHelper database_helper;
    private HttpHelper http_helper;
    private boolean running = true;
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "My Channel";
    private final String DB_NAME = "shared_list_app.db";
    @Override
    public void onCreate() {
        super.onCreate();

        http_helper = new HttpHelper();
        database_helper  = new DbHelper(this, DB_NAME, null, 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {

                        JSONArray shared_lists = http_helper.getJSONArrayFromURL(DB_NAME + "/lists");

                        for (int i = 0; i < shared_lists.length(); i++) {
                            JSONObject jsonObject = shared_lists.getJSONObject(i);
                            String title = jsonObject.getString("name");
                            String creator = jsonObject.getString("creator");
                            boolean shared = jsonObject.getBoolean("shared");

                            if (!database_helper.queryLists(title)) {
                                database_helper.addList(title, creator, true);
                            }

                            JSONArray shared_tasks = http_helper.getJSONArrayFromURL(DB_NAME + "/tasks/" + title);

                            for (int j = 0; j < shared_tasks.length(); j++) {
                                JSONObject jsonObject1 = shared_tasks.getJSONObject(j);
                                String id = jsonObject1.getString("taskId");
                                String name = jsonObject1.getString("name");
                                boolean done = jsonObject1.getBoolean("done");

                                if (!database_helper.provera(name)) {
                                    database_helper.addTask(name, title, id,"jeste");
                                }
                            }
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(30 * 1000);
                        sendNotification(getApplicationContext(), "ShopSync", "Shopping Lists Synced Successfully");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void sendNotification(Context context, String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}