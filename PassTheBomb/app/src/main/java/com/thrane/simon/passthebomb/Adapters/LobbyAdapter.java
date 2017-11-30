package com.thrane.simon.passthebomb.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.thrane.simon.passthebomb.Models.Game;
import com.thrane.simon.passthebomb.R;

import java.util.List;

/**
 * Created by Simon on 29-11-2017.
 */
//Highly inspired by Kasper Løvborgs solution for Adapter exercise
public class LobbyAdapter extends BaseAdapter {
    private FirebaseDatabase database;
    public List<Game> gameDataList;
    private Context context;
    private Game gameDataItem;


    public LobbyAdapter(FirebaseDatabase database) {
        this.database = database;
    }

    @Override
    public int getCount() {
        if(gameDataList != null){
            return gameDataList.size();
        }else{
            return 0;
        }
    }

    @Override
    public Game getItem(int position) {
        if(gameDataList != null) {
            return gameDataList.get(position);
        } else {
            return null;
        }    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //we only need to create the views once, if not null we will reuse the existing view and update its values
        if (convertView == null) {
            LayoutInflater weatherInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = weatherInflater.inflate(R.layout.lobby_list_item, null);
        }

        gameDataItem = getItem(position);
        if(gameDataItem!=null){
            //set the properties from weatherDataItem
            TextView txtLobbyName = convertView.findViewById(R.id.tvLobbyName);
            txtLobbyName.setText(gameDataItem.Name);

            TextView txtHostName = convertView.findViewById(R.id.tvHostName);
            //txtHostName.setText(Integer.toString(gameDataItem.Host.Name));

            TextView txtPlayerNum = convertView.findViewById(R.id.tvPlayerNum);
            txtPlayerNum.setText(Double.toString(gameDataItem.Users.size()));

        }
        return convertView;
    }
}
