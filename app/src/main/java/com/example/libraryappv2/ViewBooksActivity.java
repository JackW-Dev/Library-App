package com.example.libraryappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ViewBooksActivity extends AppCompatActivity {

    private UserDTO userObj;
    private ListView bookList;
    private RequestQueue requestQueue;
    private ArrayList<BookDTO> bookDTOArrayList = new ArrayList<BookDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        requestQueue = Volley.newRequestQueue(this);

        userObj = getIntent().getParcelableExtra("userObj");

        bookList = findViewById(R.id.bookList);

        String URL = "http://10.0.2.2:8080/LibraryDemoRESTful/webresources/webServices/allBooks";

        StringRequest bookListGET = new StringRequest(Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray bookArrJson = new JSONArray(response);

                            for (int i = 0; i < bookArrJson.length(); i++){
                                BookDTO tempBook = new Gson().fromJson(bookArrJson.get(i).toString(), BookDTO.class);
                                bookDTOArrayList.add(i, tempBook);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<BookDTO> bookAdapter = new ArrayAdapter<BookDTO>(ViewBooksActivity.this,
                                android.R.layout.simple_list_item_1,
                                bookDTOArrayList){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView view = (TextView) super.getView(position, convertView, parent);
                                view.setText(getItem(position).getTitle() + " - " + getItem(position).getAuthor());
                                return view;
                            }
                        };
                        bookList.setAdapter(bookAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewBooksActivity.this, "Web service error.", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(bookListGET);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(userObj.getUserType() == 1){
                    final Intent userBookIntent = new Intent(ViewBooksActivity.this, ViewBookDetailsActivity.class);

                    userBookIntent.putExtra("userObj", userObj);
                    userBookIntent.putExtra("bookObj", new Gson().toJson(bookList.getItemAtPosition(position)));

                    startActivity(userBookIntent);
                }else{
                    final Intent adminBookIntent = new Intent(ViewBooksActivity.this, ViewBookDetailsAdminActivity.class);
                    adminBookIntent.putExtra("userObj", userObj);
                    adminBookIntent.putExtra("bookObj", new Gson().toJson(bookList.getItemAtPosition(position)));

                    startActivity(adminBookIntent);
                }

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(userObj.getUserType() == 1){
            getMenuInflater().inflate(R.menu.action_bar, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeButton:
                Toast.makeText(ViewBooksActivity.this, "Home", Toast.LENGTH_SHORT).show();
                final Intent homeIntent = new Intent(this, MenuActivity.class);
                homeIntent.putExtra("userObj", userObj);
                startActivity(homeIntent);
                return true;
            case R.id.backButton:
                Toast.makeText(ViewBooksActivity.this, "Back", Toast.LENGTH_SHORT).show();
                if(userObj.getUserType() == 1){
                    final Intent backIntent = new Intent(this, MenuActivity.class);
                    backIntent.putExtra("userObj", userObj);
                    startActivity(backIntent);
                }else{
                    final Intent backIntent = new Intent(this, MainActivity.class);
                    startActivity(backIntent);
                }
                return true;
            case R.id.logOut:
                Toast.makeText(ViewBooksActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                final Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
