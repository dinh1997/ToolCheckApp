package info.androidhive.sqlite.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import info.androidhive.sqlite.R;

public class Test extends AppCompatActivity {
    Button button, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        button = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getLayoutInflater().inflate(R.layout.test_dialog, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Test.this);
                builder.setTitle("Spinner in dialog");

                final SearchableSpinner spinner = view.findViewById(R.id.spinner_test);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Test.this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.array_country));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Test.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setView(view).show();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test.this, MyService.class);
                startService(intent);
            }
        });
    }
}
