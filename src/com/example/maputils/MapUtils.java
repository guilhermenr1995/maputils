package com.example.maputils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import android.graphics.Color;
import android.os.StrictMode;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapUtils {

	public JSONObject executeRequest(String uri) throws Exception 
	{
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
	    HttpGet req = new HttpGet(uri);

	    HttpClient client = new DefaultHttpClient();
	    HttpResponse resLogin = client.execute(req);
	    BufferedReader r = new BufferedReader(
	    new InputStreamReader(resLogin.getEntity().getContent()));
	    StringBuilder sb = new StringBuilder();
	    String s = null;
	    
	    while ((s = r.readLine()) != null) 
	    {
	        sb.append(s);
	    }

	    return new JSONObject(sb.toString());
	}
	
	public String getInfo(String result)
	{
		String Res = "";
		try 
		{
			DirectionsJSONParser parser = new DirectionsJSONParser();
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			String distance = routes.getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
			String duration = routes.getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("value");
			String start_address = routes.getJSONArray("legs").getJSONObject(0).getJSONObject("start_address").toString();
			String end_address = routes.getJSONArray("legs").getJSONObject(0).getJSONObject("end_address").toString();
			Res = distance + "\n" + duration + "\n" + start_address + "\n" + end_address;
		} 
		catch (JSONException e) 
		{
			System.out.println("Erro GetInfo");
			e.printStackTrace();
		}
		System.out.println("Res " + Res);
		return Res;
	}
	    
	public void drawPath(String result, GoogleMap map) 
	{
		try 
		{
			DirectionsJSONParser parser = new DirectionsJSONParser();
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			String distance = routes.getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = parser.decodePoly(encodedString);	
			
			for (int z = 0; z < list.size() - 1; z++) 
			{
				LatLng src = list.get(z);
				LatLng dest = list.get(z + 1);
				Polyline line = map.addPolyline(new PolylineOptions()
				.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude)).width(4).color(Color.BLUE).geodesic(true));
			
				if (z == list.size() - 2)
				{
					map.addMarker(new MarkerOptions()
		            .position(new LatLng(dest.latitude, dest.longitude))
		            .title("Local de Entrega"));
				}
			}
		} 
		catch (JSONException e) 
		{
			System.out.println("Erro DrawPath");
			e.printStackTrace();
		}
	}
}
