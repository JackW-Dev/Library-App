package com.example.libraryappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private CheckBox adminStatus;
    private EditText usernameET, passwordET;
    private String usernameStr, passwordStr;
    private UserDTO userObj;
    private RequestQueue requestQueue;
    private JSONObject userJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        final Intent menuIntent = new Intent(this, MenuActivity.class);
        final Intent adminIntent = new Intent(this, ViewBooksActivity.class);

        loginButton = findViewById(R.id.logInButton);
        adminStatus = findViewById(R.id.isAdminBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameET = findViewById(R.id.username);
                passwordET = findViewById(R.id.password);

                usernameStr = usernameET.getText().toString();
                passwordStr = passwordET.getText().toString();

                userObj = new UserDTO();
                userJSON = new JSONObject();

                try {
                    userJSON.put("username", usernameStr);
                    userJSON.put("password", passwordStr);

                    if(adminStatus.isChecked()){
                        userJSON.put("userType", 2);
                    }else{
                        userJSON.put("userType", 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/login";

                JsonObjectRequest userLoginPOST = new JsonObjectRequest(Request.Method.POST,
                        URL,
                        userJSON,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    userObj.setUsername(response.getString("username"));
                                    userObj.setPassword(response.getString("password"));
                                    userObj.setUserType(response.getInt("userType"));

                                    if(userObj.getUserType() == 1){
                                        userObj.setName(response.getJSONObject("member").getString("name"));

                                        Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();

                                        menuIntent.putExtra("userObj", userObj);
                                        startActivity(menuIntent);

                                    }else if(userObj.getUserType() == 2){
                                        userObj.setName(response.getJSONObject("librarian").getString("name"));

                                        Toast.makeText(MainActivity.this, "Logged in as admin", Toast.LENGTH_SHORT).show();

                                        adminIntent.putExtra("userObj", userObj);
                                        startActivity(adminIntent);

                                    }else{
                                        Toast.makeText(MainActivity.this, "Failed to log in, please try again", Toast.LENGTH_SHORT).show();

                                        usernameET.setText("");
                                        passwordET.setText("");
                                        adminStatus.setChecked(false);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Failed to log in, please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                requestQueue.add(userLoginPOST);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MainActivity.this,"Cannot go back",Toast.LENGTH_LONG).show();
        return;
    }
}
