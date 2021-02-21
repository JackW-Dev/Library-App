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
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewLoanHistoryActivity extends AppCompatActivity {

    private UserDTO userObj;
    private RequestQueue requestQueue;
    private JSONObject userJSON;
    private ArrayList<LoanDTO> loanDTOArrayList = new ArrayList<LoanDTO>();
    private ListView pastLoansListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_loan_history);

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

        pastLoansListView = findViewById(R.id.loanHistoryList);

        String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/loanHistory";

        JsonArrayRequest loanHistoryPOST = new JsonArrayRequest(Request.Method.POST,
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

                        ArrayAdapter<LoanDTO> loanAdapter = new ArrayAdapter<LoanDTO>(ViewLoanHistoryActivity.this,
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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewLoanHistoryActivity.this);

                                        builder.setTitle("Loan Details");

                                        builder.setMessage("Title - " + getItem(position).getCopy().getBook().getTitle()
                                                + "\nAuthor - " + getItem(position).getCopy().getBook().getAuthor()
                                                + "\nCopy ID - " + getItem(position).getCopy().getId()
                                                + "\nReturned - " + getItem(position).getReturnDate().get(Calendar.DATE)
                                                + "/" + getItem(position).getReturnDate().get(Calendar.MONTH)
                                                + "/" + getItem(position).getReturnDate().get(Calendar.YEAR));

                                        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
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
                        pastLoansListView.setAdapter(loanAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewLoanHistoryActivity.this, "Web service error.", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(loanHistoryPOST);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeButton:
                Toast.makeText(ViewLoanHistoryActivity.this, "Home", Toast.LENGTH_SHORT).show();
                final Intent homeIntent = new Intent(this, MenuActivity.class);
                homeIntent.putExtra("userObj", userObj);
                startActivity(homeIntent);
                return true;
            case R.id.backButton:
                Toast.makeText(ViewLoanHistoryActivity.this, "Back", Toast.LENGTH_SHORT).show();
                final Intent backIntent = new Intent(this, MenuActivity.class);
                backIntent.putExtra("userObj", userObj);
                startActivity(backIntent);
                return true;
            case R.id.logOut:
                Toast.makeText(ViewLoanHistoryActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                final Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
