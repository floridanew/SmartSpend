// app/src/main/java/com/example/smartspend/view/HistoryActivity.java
package com.example.smartspend.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.R;
import com.example.smartspend.adapter.TransactionAdapter;
import com.example.smartspend.viewmodel.HistoryViewModel;
import com.example.smartspend.viewmodel.HistoryViewModelFactory;

import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    private HistoryViewModel viewModel;
    private TransactionAdapter adapter;

    private long startDate = -1, endDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // TODO : remplacer par l'userId réel depuis la session
        int userId = getSharedPreferences("session", MODE_PRIVATE).getInt("userId", -1);

        // ViewModel
        HistoryViewModelFactory factory = new HistoryViewModelFactory(getApplication(), userId);
        viewModel = new ViewModelProvider(this, factory).get(HistoryViewModel.class);

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);

        // Observer LiveData
        viewModel.transactions.observe(this, adapter::setTransactions);

        // Clic sur une transaction → édition (à brancher sur le Membre 3)
        adapter.setOnTransactionClickListener(transaction ->
                Toast.makeText(this, "Transaction : " + transaction.category, Toast.LENGTH_SHORT).show()
        );

        // SearchView
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                viewModel.search(newText);
                return true;
            }
        });

        // Filtre par date
        Button btnStartDate = findViewById(R.id.btn_start_date);
        Button btnEndDate   = findViewById(R.id.btn_end_date);
        Button btnReset     = findViewById(R.id.btn_reset_filter);

        btnStartDate.setOnClickListener(v -> pickDate(true, btnStartDate));
        btnEndDate.setOnClickListener(v -> pickDate(false, btnEndDate));
        btnReset.setOnClickListener(v -> {
            startDate = -1; endDate = -1;
            btnStartDate.setText("Date début");
            btnEndDate.setText("Date fin");
            viewModel.resetFilter();
        });
    }

    private void pickDate(boolean isStart, Button btn) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, day, isStart ? 0 : 23, isStart ? 0 : 59, isStart ? 0 : 59);
            long timestamp = selected.getTimeInMillis();
            String label = day + "/" + (month + 1) + "/" + year;
            btn.setText(label);
            if (isStart) startDate = timestamp;
            else endDate = timestamp;

            if (startDate != -1 && endDate != -1)
                viewModel.filterByDateRange(startDate, endDate);

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}