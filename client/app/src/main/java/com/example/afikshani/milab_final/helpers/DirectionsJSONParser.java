package com.example.afikshani.milab_final.helpers;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser {

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");


            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
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

                HashMap<String, String> rating = new HashMap<String, String>();
                String rateOfRoute = ((JSONObject) jRoutes.get(i)).getString("rate");
                rating.put("rating", rateOfRoute);
                path.add(rating);


                JSONArray warningsArray = ((JSONArray) ((JSONObject) jRoutes.get(i)).get("topThree"));

                HashMap<String, String> firstWarning = new HashMap<String, String>();
                HashMap<String, String> secondWarning = new HashMap<String, String>();
                HashMap<String, String> thirdWarning = new HashMap<String, String>();

                for (int j = 0; j < warningsArray.length(); j++) {
                    JSONObject currentWarning = (JSONObject) warningsArray.get(j);
                    String address = currentWarning.get("address") +" - ";
                    String details = (String) currentWarning.get("warning");
                    String fullWarning = address + details;
                    if(j==0) firstWarning.put("warning", fullWarning);
                    if(j==1) secondWarning.put("warning", fullWarning);
                    if(j==2) thirdWarning.put("warning", fullWarning);

                }

                path.add(firstWarning);
                path.add(secondWarning);
                path.add(thirdWarning);


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

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return routes;
    }

    private String saveWarnings(JSONArray warningsArray) {
        Warnings warnings = new Warnings();
        try {
            for (int i = 0; i < warningsArray.length(); i++) {
                JSONArray warning = (JSONArray) warningsArray.get(i);
                String warningLocation = warning.getJSONArray(0).getString(0);
                String warningDetails = warning.getString(1);
                WarningDetails newWarning = new WarningDetails(warningLocation,warningDetails);
                warnings.addWarning(newWarning);
            }
        } catch (Exception e) {
        }finally {
            return warnings.toString();
        }
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