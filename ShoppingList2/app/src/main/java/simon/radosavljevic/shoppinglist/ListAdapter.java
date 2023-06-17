package simon.radosavljevic.shoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ListItem> listItem;

    public ListAdapter(Context context){
        this.context=context;
        this.listItem= new ArrayList<ListItem>();
    }

    public void addItem(ListItem listItem1)
    {
        listItem.add(listItem1);
        notifyDataSetChanged();
    }
    public void removeItem(int i) {
        listItem.remove(i);
        notifyDataSetChanged();
    }
    public void clearList(){
        listItem.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        Object returnObject = null;
        try {
            returnObject = listItem.get(position);
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

//    public void update(ListItem[] shoppingLists){
//        listItem.clear();
//        if(shoppingLists != null){
//            for(listItem shoppingList : shoppingLists){
//                listItem.add(shoppingList);
//            }
//        }
//        notifyDataSetChanged();
//    }
//    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, null);
            viewHolder = new ViewHolder();
            viewHolder.naslov=convertView.findViewById(R.id.naslovWelcome);
            viewHolder.shared=convertView.findViewById(R.id.sharedWelcome);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ListItem item=listItem.get(position);


        viewHolder.naslov.setText(item.getNaslov());
        if(item.getShared())
        {
            viewHolder.shared.setText("true");
        }
        else
        {
            viewHolder.shared.setText("false");
        }


        return convertView;
    }
    private static class ViewHolder {
        TextView naslov;
        TextView shared;
    }
}
