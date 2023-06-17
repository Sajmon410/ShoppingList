package simon.radosavljevic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class NewList extends AppCompatActivity {
    Button ok;
    Button save;
    EditText unos;
    TextView naslov;
    RadioGroup radioGroup;
    String shared;
    Button home;
    String sh;
    TextView postojiNaslov;
    HttpHelper httpHelper;
    private static String ListsUrl = MainActivity.urlBase + "/lists";
    private final String DB_NAME = "shared_list_app.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String user = bundle.getString("name");
        setContentView(R.layout.activity_new_list);
        ok = findViewById(R.id.naslovButton);
        unos = findViewById(R.id.naslov);
        naslov = findViewById(R.id.mainNaslov);
        radioGroup = findViewById(R.id.radiobuttongroup);
        postojiNaslov = findViewById(R.id.postojiNaslov);
        shared = "true";
        DbHelper dbHelper = new DbHelper(this, DB_NAME, null, 1);
        httpHelper = new HttpHelper();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naslov.setText(unos.getText().toString());
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = findViewById(selectedId);

                String selectedText = radioButton.getText().toString();

                if (selectedText.equals("Yes")) {
                    shared = "true";

                } else {
                    shared = "false";
                }
            }
        });
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (dbHelper.readListName(unos.getText().toString()) == null) {

                    Lista lista = new Lista(naslov.getText().toString(), user.toString(), shared);
                    dbHelper.insertList(lista);

                    if (shared.equals("true")) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject requestJSON = new JSONObject();
                                    requestJSON.put("name", naslov.getText().toString());
                                    requestJSON.put("creator", user.toString());
                                    requestJSON.put("shared", shared);
                                    boolean jsonObject = httpHelper.postJSONObjectFromURL(ListsUrl, requestJSON);
                                    if (jsonObject) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                finish();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(NewList.this, "Greska pri dodavanje", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                    finish();


                } else {
                    postojiNaslov.setText("Naslov liste koji ste uneli vec posotji.");
                }


            }
        });


        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewList.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}