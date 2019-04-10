package info.androidhive.sqlite.view;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.support.design.widget.CoordinatorLayout;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.DatabaseHelper;
import info.androidhive.sqlite.database.model.Note;
import info.androidhive.sqlite.utils.MyDividerItemDecoration;
import info.androidhive.sqlite.utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {
    private NotificationCompat.Builder notiBuilder;

    private static final int MY_NOTIFICATION_ID = 12345;
    private static final int MY_REQUEST_CODE = 100;
    private static final String TAG = "jkcbsk";

    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private TextView noNotesView;

    private ArrayList<String> str_array = new ArrayList<>();
    private int number_count, number_1;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set Notification
        this.notiBuilder = new NotificationCompat.Builder(this);
        this.notiBuilder.setAutoCancel(true);

//        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);

        notesList.addAll(db.getAllNotes());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        checkAppExist();
        checkIfAnAppDeleted();

        addAutoStartup();

        Intent intent = new Intent(MainActivity.this, MyService.class);
        this.startService(intent);

//        String manufacturer = "xiaomi";
//        if(manufacturer.equalsIgnoreCase(Build.BRAND)) {
//            //this will open auto start screen where user can enable permission for your app
//            Intent intent1 = new Intent();
//            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//            startActivity(intent1);
//        }

        /*
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showActionsDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    public List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
//            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.FakeActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupmanager.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startsettings")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.startupmanager")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.startupActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.startupapp.startupmanager")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.FakeActivity"))
    );
    private void addAutoStartup() {

        boolean foundCorrectIntent = false;

        for (final Intent intents : POWERMANAGER_INTENTS) {
            if (isCallable(this, intents)) {

                Log.d(TAG, "addAutoStartup: " + intents);

                MainActivity.this.startActivity(intents);
//                new AlertDialog.Builder(this)
//                        .setTitle(Build.MANUFACTURER + " Protected Apps")
//                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", this.getString(R.string.app_name)))
////
//                        .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.this.startActivity(intents);
//                            }
//                        })
//                        .setNegativeButton(android.R.string.cancel, null)
//                        .setCancelable(false)
//                        .show();
                break;
            }
        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                checkAppExist();
                return true;
            case R.id.action_delete_all:
                checkIfAnAppDeleted();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAppExist() {
        int i = db.getNotesCount() - 1; // get number of database's row
        for (int jj = 0; jj <= i; jj++) {
            Note note1 = notesList.get(jj);
            note1.setImg_check(R.drawable.ic_add_white_24dp);
        }
        new mTask().execute(i);
    }

    private void checkIfAnAppDeleted() {
        int j = db.getNotesCount() - 1;
        int int_check_app = 0;
        for (int jj = 0; jj <= j; jj++) {
            Note note = notesList.get(jj);
            if (note.getImg_check() == R.drawable.error) {
                int_check_app = int_check_app + 1;
            }
        }

        if (int_check_app != 0) {
//            setNotification();
            Toast.makeText(this, "An App Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNotification() {
        this.notiBuilder.setSmallIcon(R.mipmap.ic_launcher);
        this.notiBuilder.setTicker("Detect an app have been deleted");

        // Sét đặt thời điểm sự kiện xẩy ra.
        // Các thông báo trên Panel được sắp xếp bởi thời gian này.
        this.notiBuilder.setWhen(System.currentTimeMillis() + 10 * 1000);
        this.notiBuilder.setContentTitle("Notification");
        this.notiBuilder.setContentText("Detect an app have been deleted ....");
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        this.notiBuilder.setSound(alarmSound);
        // Tạo một Intent
        Intent intent = new Intent(this, MainActivity.class);


        // PendingIntent.getActivity(..) sẽ start mới một Activity và trả về
        // đối tượng PendingIntent.
        // Nó cũng tương đương với gọi Context.startActivity(Intent).
        PendingIntent pendingIntent = PendingIntent.getActivity(this, MY_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        this.notiBuilder.setContentIntent(pendingIntent);

        // Lấy ra dịch vụ thông báo (Một dịch vụ có sẵn của hệ thống).
        NotificationManager notificationService =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Xây dựng thông báo và gửi nó lên hệ thống.

        Notification notification = notiBuilder.build();
        notificationService.notify(MY_NOTIFICATION_ID, notification);

    }

//    public class mService extends Service {
//        MediaPlayer mediaPlayer;
//        public mService() {
//        }
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//
//        @Override
//        public void onCreate() {
//            super.onCreate();
//            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sleepaway);
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//
//            mediaPlayer.start();
////            Timer timer = new Timer();
////            timer.scheduleAtFixedRate(new TimerTask() {
////                @Override
////                public void run() {
////                    int i = db.getNotesCount() - 1;
////                    new mTask().execute(i);
////                    Toast.makeText(mService.this, "Mother fucker", Toast.LENGTH_SHORT).show();
////                }
////            }, 0, 3000);
//            return START_STICKY;
//        }
//
//        @Override
//        public void onDestroy() {
//            mediaPlayer.release();
//            super.onDestroy();
//        }
//
//
//    }

    @SuppressLint("StaticFieldLeak")
    public class mTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {

            Document document;
            number_count = integers[0];  // = number of element of array list - 1
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
                note.setImg_check(R.drawable.error);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            checkIfAnAppDeleted();
        }
    }


    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String packKage, String keyword, String country, int status) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(packKage, keyword, country, status);

        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String pacKage, String keyword, String country, int status, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setPacKage(pacKage);
        n.setKeyword(keyword);
        n.setCountry(country);
        n.setImg_check(status);

        db.updateNote(n);        // updating note in db

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    @SuppressLint("InflateParams")
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        View view = getLayoutInflater().inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);

        final EditText inputPackage = view.findViewById(R.id.dialog_package);
        final EditText inputKeyword = view.findViewById(R.id.dialog_keyword);
        final SearchableSpinner spinner = view.findViewById(R.id.spinner_searchable);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.array_country));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setTitle("Choose country");
        spinner.setAdapter(adapter);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            inputPackage.setText(note.getPacKage());
            inputKeyword.setText(note.getKeyword());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        alertDialogBuilderUserInput.setView(view);
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show toast message when no text is entered
                String str_package = inputPackage.getText().toString();
                String str_keyword = inputKeyword.getText().toString();
                String str_country = spinner.getSelectedItem().toString();
                String str_gl_1 = "";
                switch (str_country) {
                    case "Vietnam":
                        str_gl_1 = "vi";
                        break;
                    case "USA":
                        str_gl_1 = "us";
                        break;
                    case "Myanmar":
                        str_gl_1 = "mm";
                        break;
                    case "Malaysia":
                        str_gl_1 = "my";
                        break;
                    case "Laos":
                        str_gl_1 = "la";
                        break;
                    case "Cambodia":
                        str_gl_1 = "kh";
                        break;
                    case "Indonesia":
                        str_gl_1 = "id";
                        break;
                    case "Singapore":
                        str_gl_1 = "sg";
                        break;
                    case "Philippines":
                        str_gl_1 = "ph";
                        break;
                    default:
                        break;
                }
                if (TextUtils.isEmpty(inputPackage.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(str_package, str_keyword, str_gl_1, R.drawable.error, position);
                    int i = db.getNotesCount() - 1; // get number of database's row
                    for (int jj = 0; jj <= i; jj++) {
                        Note note1 = notesList.get(jj);
                        note1.setImg_check(R.drawable.ic_add_white_24dp);
                    }
                    new mTask().execute(i);
                } else {
                    // create new note
                    createNote(str_package, str_keyword, str_gl_1, R.drawable.ic_add_white_24dp);
                    int i = db.getNotesCount() - 1; // get number of database's row
                    for (int jj = 0; jj <= i; jj++) {
                        Note note1 = notesList.get(jj);
                        note1.setImg_check(R.drawable.ic_add_white_24dp);
                    }
                    new mTask().execute(i);
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
