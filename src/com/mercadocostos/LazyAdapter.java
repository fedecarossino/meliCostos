package com.mercadocostos;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.preciomercadolibre.R;


public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.item_list, null);

        HashMap<String, String> mapData = data.get(position);
		
		TextView titulo = (TextView) vi.findViewById(R.id.title);
		TextView precioPublicar = (TextView) vi.findViewById(R.id.PrecioPublicar);
		TextView preciovender = (TextView) vi.findViewById(R.id.PrecioVender);
		TextView valorNeto = (TextView) vi.findViewById(R.id.valorNeto);
		TextView valorNetoUnidad = (TextView) vi.findViewById(R.id.valorNetoUnidad);
		
		
		titulo.setText(mapData.get("title"));
		precioPublicar.setText(mapData.get("PrecioPublicar"));
		preciovender.setText(mapData.get("PrecioVender"));
		valorNeto.setText(mapData.get("valorNeto"));
		valorNetoUnidad.setText(mapData.get("valorNetoUnidad"));
		
		if(titulo.getText().toString().contains("Premium") || titulo.getText().toString().contains("Diamante")){
			View lay = (View) vi.findViewById(R.id.layoutTittle); 
			lay.setBackgroundResource(R.drawable.gold);
		}else if(titulo.getText().toString().contains("Bronce") || titulo.getText().toString().contains("Bronze")){
			View lay = (View) vi.findViewById(R.id.layoutTittle); 
			lay.setBackgroundResource(R.drawable.bronze);
		}else if(titulo.getText().toString().contains("Plata") || titulo.getText().toString().contains("Prata")){
			View lay = (View) vi.findViewById(R.id.layoutTittle); 
			lay.setBackgroundResource(R.drawable.plate);
		}else if(titulo.getText().toString().equals("Publicaci—n Oro") || titulo.getText().toString().equals("Ouro")){
			View lay = (View) vi.findViewById(R.id.layoutTittle); 
			lay.setBackgroundResource(R.drawable.diamond);
		}
		
        return vi;
    }
}