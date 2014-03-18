package com.mercadocostos;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.preciomercadolibre.R;


public class calcularActivity extends Activity{
	
	ArrayList<HashMap<String, String>> elementsList;
	private ProgressDialog pDialog;
	Button calcular;
	TextView precio;
	ListView list;
	TextView cantidad, error;
	LazyAdapter adapter;
	private AdView adView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        elementsList = new ArrayList<HashMap<String, String>>();
 
        list=(ListView)findViewById(R.id.list);
        calcular=(Button)findViewById(R.id.button1);
        
        precio = (TextView) findViewById(R.id.editText1);
        
        
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(precio.getApplicationWindowToken(), 0);

        adView = ( AdView) findViewById(R.id.adView);
        error = (TextView) findViewById(R.id.errorText);
       
        calcular.setOnClickListener(new OnClickListener() {

	    	   @Override
	    	    public void onClick(View view) {
	    		   	precio = (TextView) findViewById(R.id.editText1);
	    		   	cantidad = (TextView) findViewById(R.id.editCantidad);
	    		   	error = (TextView) findViewById(R.id.errorText);
	    		   	if(!precio.getText().toString().equals("") && !cantidad.getText().toString().equals("")){
	    		   		error.setText("");
	    		   		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    		   		imm.hideSoftInputFromWindow(precio.getApplicationWindowToken(), 0);
	    		   		new LoadPrice().execute();
	    		   	}else{
	    		   		error.setText("ERROR: Debe ingresar cantidad y precio!!!");
	    		   		emptyLazyAdapter();
//	    		   		setListAdapter(new SimpleAdapter(calcularActivity.this, new ArrayList(),
//							R.layout.item_list, new String[] { "PrecioVender", "PrecioPublicar", "title", "valorNeto"},
//							new int[] {R.id.PrecioVender, R.id.PrecioPublicar, R.id.title, R.id.valorNeto}));
	    		   	}
	    		    adView = ( AdView) findViewById(R.id.adView);
	    		    adView.loadAd(new AdRequest());
	    	    }
		    });
        
        adView.loadAd(new AdRequest());
	}
	
    public void onDestroy() {
        // Destroy the AdView.
        adView.destroy();
        super.onDestroy();
    }
	
    private void emptyLazyAdapter(){
    	adapter=new LazyAdapter(this,  new ArrayList<HashMap<String, String>>());
		adapter.notifyDataSetChanged();
    }
    
	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class LoadPrice extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(calcularActivity.this);
			pDialog.setMessage("Loading ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Inbox JSON
		 * */
		protected String doInBackground(String... args) {
			elementsList = new ArrayList<HashMap<String, String>>();
			
			precio = (TextView) findViewById(R.id.editText1);
			cantidad = (TextView) findViewById(R.id.editCantidad);
			
			TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	        String networkOperator = tel.getNetworkOperator();
			int mcc = Integer.parseInt(networkOperator.substring(0, 3));
			if(mcc == 722){
				//argentina
				elementsList.add(calculateOroPremium(precio.getText().toString()));
				elementsList.add(calculateOro(precio.getText().toString()));
				elementsList.add(calculatePlata(precio.getText().toString()));
				elementsList.add(calculateBronce(precio.getText().toString()));
			}else if (mcc == 724){
				//brasil
				elementsList.add(calculateOroPremiumBrasil(precio.getText().toString()));
				elementsList.add(calculateOroBrasil(precio.getText().toString()));
				elementsList.add(calculatePlataBrasil(precio.getText().toString()));
				elementsList.add(calculateBronceBrasil(precio.getText().toString()));
			}else if (mcc == 334){
				//mexico
				elementsList.add(calculateOroPremiumMexico(precio.getText().toString()));
				elementsList.add(calculateOroMexico(precio.getText().toString()));
				elementsList.add(calculatePlataMexico(precio.getText().toString()));
				elementsList.add(calculateBronceMexico(precio.getText().toString()));
			}else if (mcc == 734){
				//venezuela
				elementsList.add(calculateOroPremiumVenezuela(precio.getText().toString()));
				elementsList.add(calculateOroVenezuela(precio.getText().toString()));
				elementsList.add(calculatePlataVenezuela(precio.getText().toString()));
				elementsList.add(calculateBronceVenezuela(precio.getText().toString()));
			}else if (mcc == 748){
				//uruguay
				elementsList.add(calculateOroPremiumUruguay(precio.getText().toString()));
				elementsList.add(calculateOroUruguay(precio.getText().toString()));
				elementsList.add(calculatePlataUruguay(precio.getText().toString()));
				elementsList.add(calculateBronceUruguay(precio.getText().toString()));
			}


			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					setLazy();
					list.setAdapter(adapter);
//					ListAdapter adapter = new SimpleAdapter(
//							calcularActivity.this, elementsList,
//							R.layout.item_list, new String[] { "PrecioVender", "PrecioPublicar", "title", "valorNeto", "valorNetoUnidad"},
//							new int[] {R.id.PrecioVender, R.id.PrecioPublicar, R.id.title, R.id.valorNeto, R.id.valorNetoUnidad});
//					setListAdapter(adapter);
				}
			});

		}

	}
	private void setLazy(){
		
		adapter=new LazyAdapter(this, elementsList);
		adapter.notifyDataSetChanged();
	}
	
	private HashMap<String, String> calculateOroPremium(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro Premium");
		if(precioFloatPublicar <= 4499.99) {
			map.put("PrecioPublicar", "$ 225");
			sum +=(float) 225;
			
		}else if(precioFloatPublicar >= 4500 && precioFloatPublicar <= 15999.99){
			Float aux = (float) (precioFloatPublicar*0.05);
			sum += aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 800");
			sum +=(float) 800;
		}
		
		if(precioFloatVender <= 99.99) {
			map.put("PrecioVender", "$ 6,5");
			sum +=(float) 6.5*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 100 && precioFloatVender <= 10999.99){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 715");
			sum +=(float) 715*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOro(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro");
		if(precioFloatPublicar <= 1499.99) {
			map.put("PrecioPublicar", "$ 45");
			sum +=(float) 45;
			
		}else if(precioFloatPublicar >= 1500 && precioFloatPublicar <= 15999.99){
			Float aux = (float) (precioFloatPublicar*0.03);
			sum +=aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 480");
			sum +=(float) 480;
		}
		
		if(precioFloatVender <= 99.99) {
			map.put("PrecioVender", "$ 6,5");
			sum +=(float) 6.5*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 100 && precioFloatVender <= 10999.99){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 715");
			sum +=(float) 715*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculatePlata(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Plata");
		if(precioFloatPublicar <= 499.99) {
			map.put("PrecioPublicar", "$ 5");
			sum +=(float) 5;
			
		}else if(precioFloatPublicar >= 500 && precioFloatPublicar <= 15999.99){
			Float aux = (float) (precioFloatPublicar*0.01);
			sum +=aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 160");
			sum +=(float) 160;
		}
		
		if(precioFloatVender <= 99.99) {
			map.put("PrecioVender", "$ 6,5");
			sum +=(float) 6.5*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 100 && precioFloatVender <= 10999.99){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 715");
			sum +=(float) 715*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateBronce(String precio){
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Bronce");
		map.put("PrecioPublicar", "GRATIS");
		
		if(precioFloatVender <= 99.99) {
			map.put("PrecioVender", "$ 10");
			sum +=(float) 10*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 100 && precioFloatVender <= 10999.99){
			Float aux = (float) (precioFloatVender*0.10);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 1100");
			sum +=(float) 1100*Integer.parseInt(cantidad.getText().toString());
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOroPremiumBrasil(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Diamante");
		if(precioFloatPublicar <= 2999.99) {
			map.put("PrecioPublicar", "R$ 150");
			sum +=(float) 150;
			
		}else if(precioFloatPublicar >= 3000 && precioFloatPublicar <= 12999.99){
			Float aux = (float) (precioFloatPublicar*0.05);
			sum += aux;
			map.put("PrecioPublicar", "R$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "R$ 650");
			sum +=(float) 650;
		}
		
		if(precioFloatVender <= 15.39) {
			map.put("PrecioVender", "R$ 1");
			sum +=(float) 1*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 15.40 && precioFloatVender <= 6153.99){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "R$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "R$ 400");
			sum +=(float) 400*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: R$ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: R$ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOroBrasil(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Ouro");
		if(precioFloatPublicar <= 1166.99) {
			map.put("PrecioPublicar", "R$ 35");
			sum +=(float) 35;
			
		}else if(precioFloatPublicar >= 1167 && precioFloatPublicar <= 11666.99){
			Float aux = (float) (precioFloatPublicar*0.03);
			sum +=aux;
			map.put("PrecioPublicar", "R$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "R$ 350");
			sum +=(float) 350;
		}
		
		if(precioFloatVender <= 15.39) {
			map.put("PrecioVender", "R$ 1");
			sum +=(float) 1*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 15.40 && precioFloatVender <= 6153.99){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "R$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "R$ 400");
			sum +=(float) 400*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: R$ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculatePlataBrasil(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Prata");
		if(precioFloatPublicar <= 99.99) {
			map.put("PrecioPublicar", "R$ 1");
			sum +=(float) 1;
			
		}else if(precioFloatPublicar >= 100 && precioFloatPublicar <= 12999.99){
			Float aux = (float) (precioFloatPublicar*0.01);
			sum +=aux;
			map.put("PrecioPublicar", "R$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "R$ 130");
			sum +=(float) 130;
		}
		
		if(precioFloatVender <= 15.39) {
			map.put("PrecioVender", "R$ 1");
			sum +=(float) 1*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 15.40 && precioFloatVender <= 6153.99){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "R$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "R$ 400");
			sum +=(float) 400*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: R$ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: R$ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateBronceBrasil(String precio){
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Bronze");
		map.put("PrecioPublicar", "GRATIS");
		
		if(precioFloatVender <= 9.99) {
			map.put("PrecioVender", "R$ 1");
			sum +=(float) 1*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 10 && precioFloatVender <= 3999.99){
			Float aux = (float) (precioFloatVender*0.10);
			map.put("PrecioVender", "R$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "R$ 400");
			sum +=(float) 400*Integer.parseInt(cantidad.getText().toString());
		}
		
		map.put("valorNeto", "Ganancia neta: R$ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: R$ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOroPremiumMexico(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro Premium");
		if(precioFloatPublicar <= 8999.99) {
			map.put("PrecioPublicar", "$ 450");
			sum +=(float) 450;
			
		}else if(precioFloatPublicar >= 9000 && precioFloatPublicar <= 63999.99){
			Float aux = (float) (precioFloatPublicar*0.05);
			sum += aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 3500");
			sum +=(float) 3500;
		}
		
		if(precioFloatVender <= 83.99) {
			map.put("PrecioVender", "$ 5,50");
			sum +=(float) 6*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 84 && precioFloatVender <= 33863){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 2001");
			sum +=(float) 2001*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOroMexico(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro");
		if(precioFloatPublicar <= 4799.99) {
			map.put("PrecioPublicar", "$ 168");
			sum +=(float) 168;
			
		}else if(precioFloatPublicar >= 4800 && precioFloatPublicar <= 69999.99){
			Float aux = (float) (precioFloatPublicar*0.03);
			sum +=aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 2450");
			sum +=(float) 2450;
		}
		
		if(precioFloatVender <= 83.99) {
			map.put("PrecioVender", "$ 6");
			sum +=(float) 6*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 84 && precioFloatVender <= 33863){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 2001");
			sum +=(float) 2001*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculatePlataMexico(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Plata");
		if(precioFloatPublicar <= 499.99) {
			map.put("PrecioPublicar", "$ 5");
			sum +=(float) 5;
			
		}else if(precioFloatPublicar >= 500 && precioFloatPublicar <= 69999.99){
			Float aux = (float) (precioFloatPublicar*0.01);
			sum +=aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 700");
			sum +=(float) 700;
		}
		
		if(precioFloatVender <= 83.99) {
			map.put("PrecioVender", "$ 6");
			sum +=(float) 6*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 84 && precioFloatVender <= 33863){
			Float aux = (float) (precioFloatVender*0.065);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 2001");
			sum +=(float) 2001*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateBronceMexico(String precio){
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Bronce");
		map.put("PrecioPublicar", "GRATIS");
		
		if(precioFloatVender <= 83.99) {
			map.put("PrecioVender", "$ 8,40");
			sum +=(float) 8.40*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 84 && precioFloatVender <= 33863){
			Float aux = (float) (precioFloatVender*0.10);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 3386");
			sum +=(float) 3386*Integer.parseInt(cantidad.getText().toString());
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}

	private HashMap<String, String> calculateOroPremiumVenezuela(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro Premium");
		if(precioFloatPublicar <= 7999.99) {
			map.put("PrecioPublicar", "Bs.F. 400");
			sum +=(float) 400;
			
		}else if(precioFloatPublicar >= 8000 && precioFloatPublicar <= 24999.99){
			Float aux = (float) (precioFloatPublicar*0.05);
			sum += aux;
			map.put("PrecioPublicar", "Bs.F. "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "Bs.F. 1250");
			sum +=(float) 1250;
		}
		
		if(precioFloatVender <= 99.99) {
			map.put("PrecioVender", "Bs.F. 6");
			sum +=(float) 6*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 100 && precioFloatVender <= 20839.99){
			Float aux = (float) (precioFloatVender*0.06);
			map.put("PrecioVender", "Bs.F. "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "Bs.F. 1250");
			sum +=(float) 1250*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: Bs.F. "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: Bs.F. "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOroVenezuela(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro");
		if(precioFloatPublicar <= 2999.99) {
			map.put("PrecioPublicar", "Bs.F. 90");
			sum +=(float) 90;
			
		}else if(precioFloatPublicar >= 3000 && precioFloatPublicar <= 23999.99){
			Float aux = (float) (precioFloatPublicar*0.03);
			sum +=aux;
			map.put("PrecioPublicar", "Bs.F. "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "Bs.F. 720");
			sum +=(float) 720;
		}
		
		if(precioFloatVender <= 99.99) {
			map.put("PrecioVender", "Bs.F. 6");
			sum +=(float) 6*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 100 && precioFloatVender <= 20839.99){
			Float aux = (float) (precioFloatVender*0.06);
			map.put("PrecioVender", "Bs.F. "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "Bs.F. 1250");
			sum +=(float) 1250*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: Bs.F. "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: Bs.F. "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculatePlataVenezuela(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Plata");
		if(precioFloatPublicar <= 599.99) {
			map.put("PrecioPublicar", "Bs.F. 6");
			sum +=(float) 6;
			
		}else if(precioFloatPublicar >= 600 && precioFloatPublicar <= 36999.99){
			Float aux = (float) (precioFloatPublicar*0.01);
			sum +=aux;
			map.put("PrecioPublicar", "Bs.F. "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "Bs.F. 370");
			sum +=(float) 370;
		}
		
		if(precioFloatVender <= 99.99) {
			map.put("PrecioVender", "Bs.F. 6");
			sum +=(float) 6*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 100 && precioFloatVender <= 20839.99){
			Float aux = (float) (precioFloatVender*0.06);
			map.put("PrecioVender", "Bs.F. "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "Bs.F. 1250");
			sum +=(float) 1250*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: Bs.F. "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: Bs.F. "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateBronceVenezuela(String precio){
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Bronce");
		map.put("PrecioPublicar", "GRATIS");
		
		if(precioFloatVender <= 59.99) {
			map.put("PrecioVender", "Bs.F. 6");
			sum +=(float) 6*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 60 && precioFloatVender <= 12499.99){
			Float aux = (float) (precioFloatVender*0.10);
			map.put("PrecioVender", "Bs.F. "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "Bs.F. 1250");
			sum +=(float) 1250*Integer.parseInt(cantidad.getText().toString());
		}
		
		map.put("valorNeto", "Ganancia neta: Bs.F. "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: Bs.F. "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOroPremiumUruguay(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro Premium");
		if(precioFloatPublicar <= 7199.99) {
			map.put("PrecioPublicar", "$ 360");
			sum +=(float) 360;
			
		}else if(precioFloatPublicar >= 7200 && precioFloatPublicar <= 15999.99){
			Float aux = (float) (precioFloatPublicar*0.05);
			sum += aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 800");
			sum +=(float) 800;
		}
		
		if(precioFloatVender <= 199.99) {
			map.put("PrecioVender", "$ 10");
			sum +=(float) 10*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 200 && precioFloatVender <= 43999.99){
			Float aux = (float) (precioFloatVender*0.05);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 2200");
			sum +=(float) 2200*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateOroUruguay(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Oro");
		if(precioFloatPublicar <= 3999.99) {
			map.put("PrecioPublicar", "$ 120");
			sum +=(float) 120;
			
		}else if(precioFloatPublicar >= 4000 && precioFloatPublicar <= 12999.99){
			Float aux = (float) (precioFloatPublicar*0.03);
			sum +=aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 390");
			sum +=(float) 390;
		}
		
		if(precioFloatVender <= 199.99) {
			map.put("PrecioVender", "$ 10");
			sum +=(float) 10*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 200 && precioFloatVender <= 43999.99){
			Float aux = (float) (precioFloatVender*0.05);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=(float) aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 2200");
			sum +=(float) 2200*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculatePlataUruguay(String precio){
		Float precioFloatPublicar = Float.parseFloat(precio)*Float.parseFloat(cantidad.getText().toString());
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Plata");
		if(precioFloatPublicar <= 999.99) {
			map.put("PrecioPublicar", "$ 10");
			sum +=(float) 10;
			
		}else if(precioFloatPublicar >= 1000 && precioFloatPublicar <= 9999.99){
			Float aux = (float) (precioFloatPublicar*0.01);
			sum +=aux;
			map.put("PrecioPublicar", "$ "+String.format("%.2f", aux)+" x unidad");

		}else{
			map.put("PrecioPublicar", "$ 100");
			sum +=(float) 100;
		}
		
		if(precioFloatVender <= 199.99) {
			map.put("PrecioVender", "$ 10");
			sum +=(float) 10*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 200 && precioFloatVender <= 43999.99){
			Float aux = (float) (precioFloatVender*0.05);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 2200");
			sum +=(float) 2200*Integer.parseInt(cantidad.getText().toString());;
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
	
	private HashMap<String, String> calculateBronceUruguay(String precio){
		Float precioFloatVender = Float.parseFloat(precio);
		Float sum=(float) 0.0;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", "Bronce");
		map.put("PrecioPublicar", "GRATIS");
		
		if(precioFloatVender <= 110.99) {
			map.put("PrecioVender", "$ 10");
			sum +=(float) 10*Integer.parseInt(cantidad.getText().toString());;
			
		}else if(precioFloatVender >= 111 && precioFloatVender <= 24439.99){
			Float aux = (float) (precioFloatVender*0.09);
			map.put("PrecioVender", "$ "+String.format("%.2f", aux));
			sum +=aux*Integer.parseInt(cantidad.getText().toString());

		}else{
			map.put("PrecioVender", "$ 2200");
			sum +=(float) 2200*Integer.parseInt(cantidad.getText().toString());
		}
		
		map.put("valorNeto", "Ganancia neta: $ "+String.format("%.2f", precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum));
		map.put("valorNetoUnidad", "Ganancia neta x unidad: $ "+String.format("%.2f", (precioFloatVender*Integer.parseInt(cantidad.getText().toString())-sum)/Integer.parseInt(cantidad.getText().toString())));
		
		return map;
		
	}
}
