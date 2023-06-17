package simon.radosavljevic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ZadatakAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ZadatakItem> zadatakItem;
    private final String DB_NAME = "shared_list_app.db";
    DbHelper dbHelper;
    HttpHelper httpHelper;
    private static String TasksUrl = MainActivity.urlBase + "/tasks";

    @Override
    public int getCount() {
        return zadatakItem.size();
    }

    public ZadatakAdapter(Context context) {
        this.context = context;
        this.zadatakItem = new ArrayList<ZadatakItem>();
        dbHelper = new DbHelper(this.context, DB_NAME, null, 1);
    }

    public void addItem(ZadatakItem zadatakItem1) {
        zadatakItem.add(zadatakItem1);
        notifyDataSetChanged();
    }

    public void clearList() {
        zadatakItem.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int i) {
        zadatakItem.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        Object returnObject = null;
        try {
            returnObject = zadatakItem.get(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        ZadatakItem item = zadatakItem.get(position);
        httpHelper = new HttpHelper();
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.zadatak_row, null);
            viewHolder = new ZadatakAdapter.ViewHolder();
            viewHolder.zadatak = convertView.findViewById(R.id.zadatak);
            viewHolder.cekiran = convertView.findViewById(R.id.cekiran);
            viewHolder.cekiran.setChecked(dbHelper.provera(item.getZadatak()));
            Log.d("CEKIRAN", String.valueOf(viewHolder.cekiran.isChecked()));
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ZadatakAdapter.ViewHolder) convertView.getTag();
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
                                               @Override
                                               public boolean onLongClick(View view) {
                                                   if (ShowListActivity.shared) {
                                                       new Thread(new Runnable() {
                                                           public void run() {
                                                               try {
                                                                   String MongoID = null;
                                                                   JSONArray jsonArray = httpHelper.getJSONArrayFromURL(TasksUrl + "/" + ShowListActivity.Naziv_Liste);
                                                                   for (int i = 0; i < jsonArray.length(); i++) {
                                                                       JSONObject OBJEKAT = jsonArray.getJSONObject(i);
                                                                       String ID = OBJEKAT.getString("taskId");
                                                                       if (ID.equals(item.getId().toString())) {
                                                                           MongoID = OBJEKAT.getString("_id");
                                                                       }
                                                                   }

                                                                   httpHelper.httpDelete(TasksUrl + "/" + MongoID);
                                                                   if (httpHelper != null) {
                                                                       Handler mainHandler = new Handler(context.getMainLooper());
                                                                       mainHandler.post(new Runnable() {
                                                                           public void run() {
                                                                               zadatakItem.remove(position);
                                                                               dbHelper.deleteItem(item.getId());
                                                                               notifyDataSetChanged();
                                                                           }
                                                                       });
                                                                   }


                                                               } catch (Exception e) {
                                                                   e.printStackTrace();
                                                               }

                                                           }
                                                       }).start();
                                                   } else {
                                                       zadatakItem.remove(position);
                                                       dbHelper.deleteItem(item.getId());
                                                       notifyDataSetChanged();

                                                   }
                                                   return true;
                                               }
                                           }
        );
        viewHolder.zadatak.setText(item.getZadatak());
        //viewHolder.cekiran.setChecked();
        //item.setCekiran());
        //notifyDataSetChanged();
        viewHolder.cekiran.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      //item.setCekiran(viewHolder.cekiran.isChecked());
                                                      if (ShowListActivity.shared) {
                                                          if (viewHolder.cekiran.isChecked()) {
                                                              new Thread(new Runnable() {
                                                                  public void run() {
                                                                      try {
                                                                          JSONObject requestJSON = new JSONObject();
                                                                          requestJSON.put("taskId", item.getId());
                                                                          requestJSON.put("done", true);
                                                                          boolean jsonObject = httpHelper.putJSONObjectFromURL(TasksUrl, requestJSON);
                                                                          if (jsonObject) {
                                                                              Handler mainHandler = new Handler(context.getMainLooper());
                                                                              mainHandler.post(new Runnable() {
                                                                                  public void run() {
                                                                                      dbHelper.updateItem(item.getZadatak(), "jeste");
                                                                                      viewHolder.cekiran.setChecked(true);
                                                                                  }
                                                                              });

                                                                          } else {
                                                                          }
                                                                      } catch (JSONException | IOException e) {
                                                                          e.printStackTrace();
                                                                          Log.d("TAG", "Error");
                                                                      }
                                                                  }
                                                              }).start();

                                                          } else {
                                                              new Thread(new Runnable() {
                                                                  public void run() {
                                                                      try {
                                                                          JSONObject requestJSON = new JSONObject();
                                                                          requestJSON.put("taskId", item.getId());
                                                                          requestJSON.put("done", false);
                                                                          boolean jsonObject = httpHelper.putJSONObjectFromURL(TasksUrl, requestJSON);
                                                                          if (jsonObject) {
                                                                              Handler mainHandler = new Handler(context.getMainLooper());
                                                                              mainHandler.post(new Runnable() {
                                                                                  public void run() {
                                                                                      dbHelper.updateItem(item.getZadatak(), "nije");
                                                                                      viewHolder.cekiran.setChecked(false);
                                                                                  }
                                                                              });

                                                                          } else {
                                                                          }
                                                                      } catch (JSONException | IOException e) {
                                                                          e.printStackTrace();
                                                                          Log.d("TAG", "Error");
                                                                      }
                                                                  }
                                                              }).start();
                                                          }
                                                      } else {
                                                          if (viewHolder.cekiran.isChecked()) {
                                                              dbHelper.updateItem(item.getZadatak(), "jeste");
                                                          } else {
                                                              dbHelper.updateItem(item.getZadatak(), "nije");
                                                          }
                                                      }
                                                      notifyDataSetChanged();
                                                  }
                                              }
        );
        if (viewHolder.cekiran.isChecked()) {
            viewHolder.zadatak.setPaintFlags(viewHolder.zadatak.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            viewHolder.zadatak.setPaintFlags(viewHolder.zadatak.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        }
        viewHolder.cekiran.setChecked(dbHelper.provera(item.getZadatak()));

        return convertView;
    }

    private static class ViewHolder {
        TextView zadatak;
        CheckBox cekiran;
    }
}
