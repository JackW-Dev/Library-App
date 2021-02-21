package com.example.libraryappv2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewCurrentLoansActivity extends AppCompatActivity {

    private UserDTO userObj;
    private RequestQueue requestQueue;
    private JSONObject userJSON;
    private ArrayList<LoanDTO> loanDTOArrayList = new ArrayList<LoanDTO>();
    private ListView currentLoansListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_current_loans);

        requestQueue = Volley.newRequestQueue(this);

        userObj = getIntent().getParcelableExtra("userObj");

        userJSON = new JSONObject();

        try {
            userJSON.put("username", userObj.getUsername());
            userJSON.put("password", userObj.getPassword());
            userJSON.put("userType", userObj.getUserType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray userJsonArr = new JSONArray();
        userJsonArr.put(userJSON);

        currentLoansListView = findViewById(R.id.currentLoansList);

        String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/currentLoans";

        JsonArrayRequest currentLoansPOST = new JsonArrayRequest(Request.Method.POST,
                URL,
                userJsonArr,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++){
                            try {
                                loanDTOArrayList.add(new Gson().fromJson(response.getJSONObject(i).toString(), LoanDTO.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<LoanDTO> loanAdapter = new ArrayAdapter<LoanDTO>(ViewCurrentLoansActivity.this,
                                android.R.layout.simple_list_item_1,
                                loanDTOArrayList){
                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                final TextView view = (TextView) super.getView(position, convertView, parent);
                                view.setText(getItem(position).getCopy().getBook().getTitle() + " - " +
                                        getItem(position).getCopy().getBook().getAuthor());

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewCurrentLoansActivity.this);

                                        builder.setTitle("Loan Details");

                                        if(getItem(position).isRenewable()){
                                            builder.setPositiveButton("Renew", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    JSONObject infoJson = new JSONObject();
                                                    try {
                                                        infoJson.put("username", userObj.getUsername());
                                                        infoJson.put("password", userObj.getPassword());
                                                        infoJson.put("userType", userObj.getUserType());
                                                        infoJson.put("name", userObj.getName());

                                                        infoJson.put("id", getItem(position).getId());
                                                        infoJson.put("numberOfRenewals", getItem(position).getNumberOfRenewals());
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/renewLoan";

                                                    JsonObjectRequest renewLoanPUT = new JsonObjectRequest(Request.Method.PUT,
                                                            URL,
                                                            infoJson,
                                                            null, null);

                                                    requestQueue.add(renewLoanPUT);

                                                    recreate();
                                                }
                                            });
                                        }
                                        builder.setMessage("Title - " + getItem(position).getCopy().getBook().getTitle()
                                                + "\nAuthor - " + getItem(position).getCopy().getBook().getAuthor()
                                                + "\nCopy ID - " + getItem(position).getCopy().getId()
                                                + "\nLoan date - " + getItem(position).getLoanDate().get(Calendar.DATE)
                                                + "/" + getItem(position).getLoanDate().get(Calendar.MONTH)
                                                + "/" + getItem(position).getLoanDate().get(Calendar.YEAR)
                                                + "\nDue date - " + getItem(position).getDueDate().get(Calendar.DATE)
                                                + "/" + getItem(position).getDueDate().get(Calendar.MONTH)
                                                + "/" + getItem(position).getDueDate().get(Calendar.YEAR)
                                                + "\nRenewals - " + getItem(position).getNumberOfRenewals());

                                        builder.setNegativeButton("Return", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                JSONObject infoJson = new JSONObject();
                                                try {
                                                    infoJson.put("username", userObj.getUsername());
                                                    infoJson.put("password", userObj.getPassword());
                                                    infoJson.put("userType", userObj.getUserType());
                                                    infoJson.put("name", userObj.getName());

                                                    infoJson.put("id", getItem(position).getId());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/returnCopy";

                                                JsonObjectRequest renewLoanPUT = new JsonObjectRequest(Request.Method.PUT,
                                                        URL,
                                                        infoJson,
                                                        null, null);

                                                requestQueue.add(renewLoanPUT);

                                                recreate();
                                            }
                                        });

                                        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        AlertDialog dialog = builder.create();

                                        dialog.show();
                                    }
                                });

                                return view;
                            }
                        };
                        currentLoansListView.setAdapter(loanAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewCurrentLoansActivity.this, "Web service error.", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(currentLoansPOST);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeButton:
                Toast.makeText(ViewCurrentLoansActivity.this, "Home", Toast.LENGTH_SHORT).show();
                final Intent homeIntent = new Intent(this, MenuActivity.class);
                homeIntent.putExtra("userObj", userObj);
                startActivity(homeIntent);
                return true;
            case R.id.backButton:
                Toast.makeText(ViewCurrentLoansActivity.this, "Back", Toast.LENGTH_SHORT).show();
                final Intent backIntent = new Intent(this, MenuActivity.class);
                backIntent.putExtra("userObj", userObj);
                startActivity(backIntent);
                return true;
            case R.id.logOut:
                Toast.makeText(ViewCurrentLoansActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                final Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
