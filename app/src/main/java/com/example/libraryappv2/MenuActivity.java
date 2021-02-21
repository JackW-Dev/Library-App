package com.example.libraryappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    private TextView nameTV, usernameTV;
    private Button viewBooksButton, viewLoansButton, viewLoanHistoryButton;
    private UserDTO userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Intent viewBooksIntent = new Intent(this, ViewBooksActivity.class);
        final Intent viewLoansIntent = new Intent(this, ViewCurrentLoansActivity.class);
        final Intent viewLoanHistoryIntent = new Intent(this, ViewLoanHistoryActivity.class);

        userObj = getIntent().getParcelableExtra("userObj");

        nameTV = findViewById(R.id.nameOutput);
        usernameTV = findViewById(R.id.usernameOutput);

        nameTV.setText(userObj.getName());
        usernameTV.setText(userObj.getUsername());

        viewBooksButton = findViewById(R.id.viewBooks);
        viewLoansButton = findViewById(R.id.currentLoans);
        viewLoanHistoryButton = findViewById(R.id.loanHistory);

        viewBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "Viewing All Books", Toast.LENGTH_SHORT).show();

                viewBooksIntent.putExtra("userObj", userObj);
                startActivity(viewBooksIntent);
            }
        });
        viewLoansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "Viewing Ongoing Loans", Toast.LENGTH_SHORT).show();

                viewLoansIntent.putExtra("userObj", userObj);
                startActivity(viewLoansIntent);
            }
        });
        viewLoanHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "Viewing Loan History", Toast.LENGTH_SHORT).show();

                viewLoanHistoryIntent.putExtra("userObj", userObj);
                startActivity(viewLoanHistoryIntent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                Toast.makeText(MenuActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                final Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
