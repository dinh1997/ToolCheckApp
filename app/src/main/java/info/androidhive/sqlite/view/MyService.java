package info.androidhive.sqlite.view;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.DatabaseHelper;
import info.androidhive.sqlite.database.model.Note;

public class MyService extends Service {
    int number_count, number_1, int_result;
    List<Note> notesList = new ArrayList<>();
    ArrayList<String> str_array = new ArrayList<>();
    NotesAdapter mAdapter;
    private NotificationCompat.Builder notiBuilder;
    public Runnable mRunnable = null;
    private static final int MY_NOTIFICATION_ID = 12345;
    private static final int MY_REQUEST_CODE = 100;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        db = new DatabaseHelper(getApplicationContext());
//        notesList.addAll(db.getAllNotes());
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                int i = db.getNotesCount() - 1;
//                new mTask().execute(i);
//            }
//        }, 0, 10000);

        final Handler mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                notesList.addAll(databaseHelper.getAllNotes());

                mAdapter = new NotesAdapter(getApplicationContext(), notesList);
//                Toast.makeText(MyService.this, databaseHelper.getNotesCount() + "", Toast.LENGTH_SHORT).show();
                int i = databaseHelper.getNotesCount() - 1;
                databaseHelper.close();
                int_result = 0;
                new mTask().execute(i);
                mHandler.postDelayed(mRunnable, 15 * 1000);
            }
        };
        mHandler.postDelayed(mRunnable, 10 * 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("StaticFieldLeak")
    public class mTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {

            Document document;
            number_count = integers[0];  // = number of element of array list - 1

//            Note note1 = notesList.get(1);
//            String str = note1.getPacKage();
            for (number_1 = 0; number_1 <= number_count; number_1++) {
                Note note = notesList.get(number_1);
                String url = "https://play.google.com/store/search?q=" + note.getKeyword() + "&c=apps&gl=" + note.getCountry();
                try {
                    document = Jsoup.connect(url).get();
                    Elements elements = document.select("a.title");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Element element : elements) {
                        String href = element.attr("href");
                        String[] output = href.split("=");
                        stringBuilder.append("\n").append(output[1]);
                        str_array.add(output[1]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                publishProgress(number_1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int int_update = values[0];   // get int_update = number_1
            Note note = notesList.get(int_update);
            String str_package = note.getPacKage();
            if (str_array.contains(str_package)) {
                note.setImg_check(R.drawable.check_ok);
                mAdapter.notifyDataSetChanged();
            } else {
                int_result++;
                note.setImg_check(R.drawable.error);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int h = int_result;
            notiBuilder = new NotificationCompat.Builder(getApplicationContext());
            if (h != 0) {
//                Toast.makeText(MyService.this, h + "", Toast.LENGTH_SHORT).show();
                setNotification(String.valueOf(h));
            }
        }

    }

    private void setNotification(String i) {
        this.notiBuilder.setSmallIcon(R.mipmap.ic_launcher);
        this.notiBuilder.setTicker("Detect " + i + " apps have been deleted");

        this.notiBuilder.setWhen(System.currentTimeMillis() + 10 * 1000);
        this.notiBuilder.setContentTitle("Notification");
        this.notiBuilder.setContentText("Detect " + i + " apps have been deleted ...");
        this.notiBuilder.setAutoCancel(true);
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        this.notiBuilder.setSound(alarmSound);

        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, MY_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.notiBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationService =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = notiBuilder.build();
        notificationService.notify(MY_NOTIFICATION_ID, notification);

    }
}
