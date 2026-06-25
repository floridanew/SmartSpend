// app/src/main/java/com/example/smartspend/viewmodel/HistoryViewModel.java
package com.example.smartspend.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.smartspend.database.AppDatabase;
import com.example.smartspend.database.TransactionDao;
import com.example.smartspend.model.Transaction;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final TransactionDao dao;
    private final int userId;

    // Paramètres de filtrage observables
    private final MutableLiveData<long[]> dateRange = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    // LiveData final exposé à l'Activity
    public final LiveData<List<Transaction>> transactions;

    public HistoryViewModel(@NonNull Application application, int userId) {
        super(application);
        this.userId = userId;
        dao = AppDatabase.getInstance(application).transactionDao();

        // Transformation : selon l'état des filtres, choisir la bonne requête
        transactions = Transformations.switchMap(dateRange, range -> {
            if (range == null) {
                String q = searchQuery.getValue();
                if (q != null && !q.isEmpty())
                    return dao.searchTransactions(userId, q);
                return dao.getAllTransactions(userId);
            }
            return dao.getTransactionsByDateRange(userId, range[0], range[1]);
        });
    }

    public void filterByDateRange(long start, long end) {
        dateRange.setValue(new long[]{start, end});
    }

    public void resetFilter() {
        dateRange.setValue(null);
    }

    public void search(String query) {
        searchQuery.setValue(query);
        dateRange.setValue(null); // reset filtre date si on cherche
    }
}