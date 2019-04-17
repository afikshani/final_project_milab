package com.example.afikshani.milab_final.helpers;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class RouteCalc extends IntentService {

    private static final String ACTION_VOLLEY = "com.example.afikshani.milab_final.action.ACTION_VOLLEY";

    private static final String PARAM1_ORIGIN = "com.example.afikshani.milab_final.extra.PARAM1";
    private static final String PARAM2_DESTINATION = "com.example.afikshani.milab_final.extra.PARAM2";
    private static final String RESPONSE_PARAM = "com.example.afikshani.milab_final.extra.RESPONSE";
    public static final String RESPONSE_ANSWER = "com.example.afikshani.milab_final.extra.ANSWER";


    public RouteCalc() {
        super("RouteCalc");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void initService(Context context, String destination) {
        Intent intent = new Intent(context, RouteCalc.class);
        intent.setAction(ACTION_VOLLEY);
        intent.putExtra(PARAM1_ORIGIN, "IDC Herzliya");
        intent.putExtra(PARAM2_DESTINATION, destination);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_VOLLEY.equals(action)) {
                String origin = intent.getStringExtra(PARAM1_ORIGIN);
                String destination = intent.getStringExtra(PARAM2_DESTINATION);
                handleActionProvideRoute(origin, destination);
                //final ResultReceiver receiver = intent.getParcelableExtra(RESPONSE_PARAM);

            }

        }

    }


    private void handleActionProvideRoute(String origin, String destination) {

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=AIzaSyA55Fgqx8yShAamvF7B3llMO3ZrIKBZyAs" + "&alternatives=" + true;

        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("hello", "Response back from server - " + response);
                final Bundle bundle = new Bundle();
                bundle.putString(RESPONSE_ANSWER, response);
                //receiver.send(200, bundle);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("routes", "Error - " + error);
            }
        });

        queue.add(req);
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
