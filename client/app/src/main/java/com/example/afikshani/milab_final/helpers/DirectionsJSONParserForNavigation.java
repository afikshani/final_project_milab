package com.example.afikshani.milab_final.helpers;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParserForNavigation {


    protected static int COLOR_GREEN = Color.parseColor("#1de9b6");
    protected static int COLOR_YELLOW = Color.parseColor("#fbc02d");
    protected static int COLOR_RED = Color.parseColor("#f44336");

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject, int choosedColor) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");


            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {

                if (correctColor(choosedColor, (JSONObject) jRoutes.get(i))) {
                    List path = new ArrayList<HashMap<String, String>>();
                    // adding color to the path
                    String colorOfRoute = ((JSONObject) jRoutes.get(i)).getString("color");
                    HashMap<String, String> color = new HashMap<String, String>();
                    if (colorOfRoute.equals("0")) {
                        color.put("color", "GREEN");
                    } else if (colorOfRoute.equals("1")) {
                        color.put("color", "YELLOW");
                    } else {
                        color.put("color", "RED");
                    }
                    path.add(color);

                    String rateOfRoute = ((JSONObject) jRoutes.get(i)).getString("rate");
                    HashMap<String, String> rating = new HashMap<String, String>();
                    rating.put("rating", rateOfRoute);
                    path.add(rating);


                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }

                        Log.d("hey", "debugging it");
                        routes.add(path);


                    }


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return routes;
    }

    private boolean correctColor(int choosedColor, JSONObject jRoutes) throws JSONException {
        String colorToCheck = pasreColor(choosedColor);
        String colorOfRoute = jRoutes.getString("color");
        if (colorOfRoute.equals(colorToCheck)){
            return true;
        }

        return  false;
    }

    private String pasreColor(int choosedColor) {
        String colorString = "0";
        if (choosedColor == COLOR_YELLOW){
            colorString = "1";
        }
        if (choosedColor == COLOR_RED){
            colorString = "2";
        }
        return  colorString;
    }

    /**
     * Method to decode polyline points
     */

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}