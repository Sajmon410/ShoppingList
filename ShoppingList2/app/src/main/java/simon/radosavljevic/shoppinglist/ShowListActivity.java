package simon.radosavljevic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class ShowListActivity extends AppCompatActivity {
    TextView Naslov;
    private final String DB_NAME = "shared_list_app.db";
    private static String ListsUrl = MainActivity.urlBase + "/lists";
    Button Add;
    EditText unos;
    CheckBox box;
    String naslov;
    Button refresh;
    String user;
    public static Boolean shared;
    Button home;
    ZadatakAdapter adapter;
    DbHelper dbHelper;
    Zadatak[] upisiZadatak;
    HttpHelper httpHelper;
    public static String Naziv_Liste;
    public static String TasksUrl = MainActivity.urlBase + "/tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        httpHelper = new HttpHelper();
        ZadatakAdapter adapter = new ZadatakAdapter(this);
        Naslov = findViewById(R.id.naslovShow);
        Bundle bundle = getIntent().getExtras();
        naslov = bundle.getString("naslov");
        shared = bundle.getBoolean("shared");
        user = bundle.getString("user");
        Naslov.setText(naslov);
        refresh = findViewById(R.id.refresh);
        DbHelper dbHelper = new DbHelper(this, DB_NAME, null, 1);

        if (shared) {
            refresh.setEnabled(true);

            refresh.setOnClickListener(view -> {
                adapter.clearList();
                adapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    public void run() {
                        try {
//                                requestJSON.put("name",naslov.getText().toString() );
//                                requestJSON.put("creator", user.toString());
//                                requestJSON.put("shared", shared);
                            JSONArray jsonArray = httpHelper.getJSONArrayFromURL(ListsUrl);

                            ListItem[] lista = new ListItem[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);
                                String title = item.getString("name");
                                String creator = item.getString("creator");
                                if (title.equals(naslov)) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            new Thread(new Runnable() {
                                                public void run() {
                                                    try {
//                                requestJSON.put("name",naslov.getText().toString() );
//                                requestJSON.put("creator", user.toString());
//                                requestJSON.put("shared", shared);

                                                        JSONArray jsonArray = httpHelper.getJSONArrayFromURL(TasksUrl + "/" + naslov);

                                                        ListItem[] lista = new ListItem[jsonArray.length()];
                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                            JSONObject item = jsonArray.getJSONObject(i);
                                                            String name = item.getString("name");
                                                            String done = item.getString("done");
                                                            String id = item.getString("taskId");

                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    Boolean uradjen = false;
                                                                    if (done.equals("true")) {
                                                                        uradjen = true;
                                                                    }
                                                                    adapter.addItem(new ZadatakItem(name, uradjen, id));
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            });

                                                        }


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();

                                        }
                                    });
                                }


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            });
        }
        else{
                upisiZadatak = dbHelper.mojiZadaci(naslov);
                if (upisiZadatak != null) {
                    for (int i = 0; i < upisiZadatak.length; i++) {
                        Boolean cekiran = upisiZadatak[i].getOtkacena().equals("jeste");
                        adapter.addItem(new ZadatakItem(upisiZadatak[i].getNaziv(), cekiran, upisiZadatak[i].getID()));
                    }
                }
            }


            ///Popunjavanje
        /*
        new Thread(new Runnable() {
            public void run() {
                try {
//                                requestJSON.put("name",naslov.getText().toString() );
//                                requestJSON.put("creator", user.toString());
//                                requestJSON.put("shared", shared);

                    JSONArray jsonArray = httpHelper.getJSONArrayFromURL(TasksUrl+"/"+naslov);

                    ListItem[] lista = new ListItem[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String name = item.getString("name");
                        String done = item.getString("done");
                        String id = item.getString("taskId");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Boolean uradjen = false;
                                if (done.equals("true")) {
                                    uradjen = true;
                                }
                                adapter.addItem(new ZadatakItem(name, true,id));
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();*/
            adapter.notifyDataSetChanged();
            ListView list = findViewById(R.id.listaZadataka);
            list.setAdapter(adapter);

            unos = findViewById(R.id.unos);

            Add = findViewById(R.id.add);
            Naziv_Liste = naslov;
            Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!unos.getText().toString().trim().equals("")) {
                        if (shared) {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        String naslovZadataka = unos.getText().toString();
                                        String uID = UUID.randomUUID().toString();

                                        naslov = bundle.getString("naslov");
                                        JSONObject requestJSON = new JSONObject();
                                        requestJSON.put("name", naslovZadataka);
                                        requestJSON.put("list", naslov);
                                        requestJSON.put("done", false);
                                        requestJSON.put("taskId", uID);
                                        boolean jsonArray = httpHelper.postJSONObjectFromURL(TasksUrl, requestJSON);
                                        if (jsonArray) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {

                                                    String naslovZadataka = unos.getText().toString();
                                                    String uID = UUID.randomUUID().toString();
                                                    Zadatak zadatak = new Zadatak(naslovZadataka, naslov, "nije", uID);
                                                    dbHelper.insertItem(zadatak);
                                                    adapter.addItem(new ZadatakItem(unos.getText().toString(), false, uID));
                                                    unos.setText("");
                                                }
                                            });
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Log.d("TAG", "ERROR");
                                                }
                                            });
                                        }

                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                        ;
                                    }
                                }
                            }).start();


                        } else {
                            String naslovZadataka = unos.getText().toString();
                            String uID = UUID.randomUUID().toString();
                            Zadatak zadatak = new Zadatak(naslovZadataka, naslov, "nije", uID);
                            dbHelper.insertItem(zadatak);
                            adapter.addItem(new ZadatakItem(unos.getText().toString(), false, uID));
                            unos.setText("");
                        }
                    }
                }
            });
            home = findViewById(R.id.home);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShowListActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }