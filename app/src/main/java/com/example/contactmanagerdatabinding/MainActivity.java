package com.example.contactmanagerdatabinding;

import static androidx.core.app.ActivityCompat.setLocusContext;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Delete;
import androidx.room.Room;

import com.example.contactmanagerdatabinding.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private ContactAppDataBase contactAppDataBase;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ContactDataAdapter contactDataAdapter;

    //  binding
    private ActivityMainBinding activityMainBinding;
    private MainActivityClickHandlers handlers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //  data binding
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        handlers = new MainActivityClickHandlers(this);
        activityMainBinding.setClickHandler(handlers);

        // recycler
        //RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RecyclerView recyclerView = activityMainBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //  adapter
        contactDataAdapter = new ContactDataAdapter(contacts);


        //  database
        contactAppDataBase = Room.databaseBuilder(
                getApplicationContext(),
                ContactAppDataBase.class,
                "ContactDB")
                .allowMainThreadQueries()
                .build();

        //  add data
        LoadData();

        recyclerView.setAdapter(contactDataAdapter);

        // handling swiping
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact = contacts.get(viewHolder.getAdapterPosition());
                DeleteContact(contact);
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 1 && resultCode == RESULT_OK){
            String name = data.getStringExtra("NAME");
            String phone = data.getStringExtra("PHONE");
            String email = data.getStringExtra("EMAIL");

            Contact contact = new Contact(name, phone, email,0);

            AddNewContact(contact);
        }
    }

    private void DeleteContact(Contact contact) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // OnBackground
                contactAppDataBase.getContactDao().delete(contact);
                contacts.remove(contact);

                // On Post Execution
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactDataAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void LoadData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // OnBackground
                contacts.addAll(contactAppDataBase.getContactDao().getAllContacts());

                // On Post Execution
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactDataAdapter.setContacts(contacts);
                        contactDataAdapter.notifyDataSetChanged();

                    }
                });
            }
        });
    }

    private void AddNewContact(Contact contact){


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // OnBackground
                contactAppDataBase.getContactDao().insert(contact);
                contacts.add(contact);

                // On Post Execution
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactDataAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    public class MainActivityClickHandlers {

        Context context;

        public MainActivityClickHandlers(Context context) {
            this.context = context;
        }

        public void onFABClicked(View view) {
            Intent i = new Intent(MainActivity.this, AddNewContactActivity.class);
            startActivityForResult(i, 1);
        }
    }
}


