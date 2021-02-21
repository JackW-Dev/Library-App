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

public class ViewBookDetailsActivity extends AppCompatActivity {

    private UserDTO userObj;
    private BookDTO bookObj;
    private RequestQueue requestQueue;
    private ArrayList<CopyDTO> copyDTOArrayList = new ArrayList<CopyDTO>();
    private TextView titleTV, authorTV, isbnTV, copiesTV;
    private JSONObject bookJson;
    private ListView copiesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book_details);

        requestQueue = Volley.newRequestQueue(this);

        userObj = getIntent().getParcelableExtra("userObj");
        bookObj = new Gson().fromJson(getIntent().getStringExtra("bookObj"), BookDTO.class);

        titleTV = findViewById(R.id.title);
        titleTV.setText("Title: " + bookObj.getTitle());

        authorTV = findViewById(R.id.author);
        authorTV.setText("Author: " + bookObj.getAuthor());

        isbnTV = findViewById(R.id.isbn);
        isbnTV.setText("ISBN: " + bookObj.getIsbn());

        copiesTV = findViewById(R.id.copies);

        copiesListView = findViewById(R.id.copiesListView);

        try {
            bookJson = new JSONObject(new Gson().toJson(bookObj));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/bookDetails";

        JSONArray copyJsonArr = new JSONArray();
        copyJsonArr.put(bookJson);

        JsonArrayRequest bookCopiesPOST = new JsonArrayRequest(Request.Method.POST,
                URL,
                copyJsonArr,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++){
                            try {
                                copyDTOArrayList.add(new Gson().fromJson(response.getJSONObject(i).toString(), CopyDTO.class));
                                copiesTV.setText("Copies: " + copyDTOArrayList.size());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<CopyDTO> copyAdapter = new ArrayAdapter<CopyDTO>(ViewBookDetailsActivity.this,
                                android.R.layout.simple_list_item_1,
                                copyDTOArrayList){
                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                TextView view = (TextView) super.getView(position, convertView, parent);
                                view.setText(getItem(position).getId() + " - " + getItem(position).getStatus());

                                view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(getItem(position).isOnLoan() || getItem(position).isReferenceOnly()){
                                                    Toast.makeText(ViewBookDetailsActivity.this, "This copy is not currently available.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(ViewBookDetailsActivity.this, "This copy is available.", Toast.LENGTH_SHORT).show();

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewBookDetailsActivity.this);

                                                    builder.setTitle("Copy Details");

                                                    builder.setMessage("Title - " + bookObj.getTitle()
                                                            + "\nAuthor - " + bookObj.getAuthor()
                                                            + "\nCopy ID - " + getItem(position).getId());

                                                    builder.setPositiveButton("Borrow", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            JSONObject infoJson = new JSONObject();
                                                            try {
                                                                infoJson.put("username", userObj.getUsername());
                                                                infoJson.put("password", userObj.getPassword());
                                                                infoJson.put("userType", userObj.getUserType());
                                                                infoJson.put("name", userObj.getName());
                                                                infoJson.put("id", getItem(position).getId());
                                                                infoJson.put("onLoan", getItem(position).isOnLoan());
                                                                infoJson.put("reference", getItem(position).isReferenceOnly());
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                            String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/borrowBook";

                                                            JsonObjectRequest borrowCopyPUT = new JsonObjectRequest(Request.Method.PUT,
                                                                    URL,
                                                                    infoJson,
                                                                    null, null);

                                                            requestQueue.add(borrowCopyPUT);

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
                                    }
                                });
                                return view;
                            }
                        };
                        copiesListView.setAdapter(copyAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewBookDetailsActivity.this, "Web service error.", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(bookCopiesPOST);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeButton:
                Toast.makeText(ViewBookDetailsActivity.this, "Home", Toast.LENGTH_SHORT).show();
                final Intent homeIntent = new Intent(this, MenuActivity.class);
                homeIntent.putExtra("userObj", userObj);
                startActivity(homeIntent);
                return true;
            case R.id.backButton:
                Toast.makeText(ViewBookDetailsActivity.this, "Back", Toast.LENGTH_SHORT).show();
                final Intent backIntent = new Intent(this, ViewBooksActivity.class);
                backIntent.putExtra("userObj", userObj);
                startActivity(backIntent);
                return true;
            case R.id.logOut:
                Toast.makeText(ViewBookDetailsActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                final Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
