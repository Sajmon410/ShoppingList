package simon.radosavljevic.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {
    TextView userText;
    Button new_list;
    Button see_my_list;
    private final String DB_NAME = "shared_list_app.db";
    private static String ListsUrl = MainActivity.urlBase + "/lists";
    ListAdapter adapter;
    Lista[] l;
    Lista[] nova;
    Boolean temp = true;
    Lista[] upisiLista;
    Bundle bundle;
    String usr;
    Button home;
    Button see_shared_lists;
    Boolean firstTime = true;
    HttpHelper httpHelper;
    DbHelper dbHelper = new DbHelper(this, DB_NAME, null, 1);

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTime) {
            Lista trenutna = new Lista("", usr, "1");
            upisiLista = dbHelper.mojeIDeljene(trenutna);
            l = upisiLista;
            adapter.clearList();
            if (upisiLista != null) {
                for (int i = 0; i < upisiLista.length; i++) {
                    System.out.println(upisiLista[i].getShared());
                    if (upisiLista[i].getShared().equals("true")) {
                        temp = true;
                    } else if (upisiLista[i].getShared().equals("false")) {
                        temp = false;
                    }
                    adapter.addItem(new ListItem(upisiLista[i].getListName(), temp));

                }
            }
            adapter.notifyDataSetChanged();

//
//            Lista trenutna = new Lista("", usr, "1");
//            upisiLista = dbHelper.mojeIDeljene(trenutna);
//            adapter.notifyDataSetChanged();
//            Log.d("TAG", "Sisanje kurca");
//            temp = upisiLista[upisiLista.length - 1].getShared().equals("true");
//            adapter.addItem(new ListItem(upisiLista[upisiLista.length - 1].getListName(), temp));

        } else firstTime = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);

        adapter = new ListAdapter(this);
        httpHelper = new HttpHelper();
        new_list = findViewById(R.id.newlist);
        userText = findViewById(R.id.user);
        see_my_list = findViewById(R.id.seemylist);
        bundle = getIntent().getExtras();
        userText.setText(bundle.getString("name"));
        usr = bundle.getString("name");
        see_shared_lists = findViewById(R.id.see_shared_list);


        Lista trenutna = new Lista("", usr, "1");
        upisiLista = dbHelper.mojeIDeljene(trenutna);
        l = upisiLista;
        if (upisiLista != null) {
            for (int i = 0; i < upisiLista.length; i++) {
                System.out.println(upisiLista[i].getShared());
                if (upisiLista[i].getShared().equals("true")) {
                    temp = true;
                } else if (upisiLista[i].getShared().equals("false")) {
                    temp = false;
                }
                adapter.addItem(new ListItem(upisiLista[i].getListName(), temp));
            }
        }


        ListView list = findViewById(R.id.lista);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem li = (ListItem) adapter.getItem(i);

                dbHelper.deleteListe(li.getNaslov());
                //delete items from liste
                dbHelper.deleteItemsFromList(li.getNaslov());
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            httpHelper.httpDelete(ListsUrl + "/" + usr + "/" + li.getNaslov());
                            if (httpHelper != null) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        adapter.removeItem(i);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(WelcomeActivity.this, "Greska", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem li = (ListItem) adapter.getItem(i);

                String Naslov = li.getNaslov();
                Boolean Shared = li.getShared();
                Intent intent = new Intent(WelcomeActivity.this, ShowListActivity.class);
                Bundle bund = new Bundle();
                bund.putString("user",usr);
                bund.putString("naslov", Naslov);
                bund.putBoolean("shared", Shared);
                intent.putExtras(bund);
                startActivity(intent);

            }
        });


        new_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                builder.setTitle("New List Dialog");
                builder.setMessage("Are you sure you want to create a new list?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(WelcomeActivity.this, NewList.class);
                        Bundle bund = new Bundle();
                        bund.putString("name", userText.getText().toString());
                        intent.putExtras(bund);
                        startActivity(intent);

                    }
                });
                AlertDialog dijalog = builder.create();
                dijalog.show();
            }
        });

        see_my_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l = upisiLista;
                adapter.clearList();
                /*
                adapter.isEmpty();
                if (l != null) {
                    for (int i = l.length - 1; i >= 0; i--) {
                        adapter.removeItem(i);
                    }*/
                adapter.notifyDataSetChanged();

                Lista trenutna = new Lista("", usr, "1");
                Lista[] upisiLista = dbHelper.mojeListe(trenutna);
                if (upisiLista != null) {
                    for (int i = 0; i < upisiLista.length; i++) {
                        System.out.println(upisiLista[i].getShared());
                        if (upisiLista[i].getShared().equals("true")) {
                            temp = true;
                        } else if (upisiLista[i].getShared().equals("false")) {
                            temp = false;
                        }

                        adapter.addItem(new ListItem(upisiLista[i].getListName(), temp));
                    }
                }
            }

        });
        see_shared_lists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l = upisiLista;
                adapter.clearList();
               /* if (l != null) {
                    for (int i = l.length - 1; i >= 0; i--) {
                        adapter.removeItem(i);
                    }*/
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
                                String user = item.getString("creator");
                                boolean shared = item.getBoolean("shared");
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        adapter.addItem(new ListItem(title, shared));
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

        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
