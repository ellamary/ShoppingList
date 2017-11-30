package com.ait.android.shoppinglist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ait.android.shoppinglist.adapter.ShoppingListRecyclerAdapter;
import com.ait.android.shoppinglist.touch.ItemTouchHelperCallback;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_ID = "KEY_ITEM_ID";
    public static final int REQUEST_CODE_EDIT = 1001;

    private ShoppingListRecyclerAdapter adapter;

    private int positionToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFab();

        ((ItemApplication)getApplication()).openRealm();

        RecyclerView recyclerViewShoppingList = (RecyclerView) findViewById(R.id.recyclerShoppingList);
        adapter = new ShoppingListRecyclerAdapter(this, ((ItemApplication)getApplication()).getRealmShoppingList());

        recyclerViewShoppingList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewShoppingList.setHasFixedSize(true);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewShoppingList);

        recyclerViewShoppingList.setAdapter(adapter);

        setTotalText(adapter.getTotalUnpurchased());

    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdd = new Intent();
                intentAdd.setClass(MainActivity.this, CreateNewItemActivity.class);
                startActivity(intentAdd);
            }
        });
    }

    public void setTotalText(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        TextView tvTotal = findViewById(R.id.tvTotal);
        tvTotal.setText(getString(R.string.total) + formatter.format(price));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_all:
                adapter.deleteAllItems();
                return true;
            case R.id.add_item:
                Intent intentAdd = new Intent();
                intentAdd.setClass(MainActivity.this, CreateNewItemActivity.class);
                startActivity(intentAdd);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openEditActivity(int adapterPosition, String itemId) {
        positionToEdit = adapterPosition;

        Intent intentEdit = new Intent(this, CreateNewItemActivity.class);
        intentEdit.putExtra(KEY_ITEM_ID, itemId);
        startActivityForResult(intentEdit, REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            String itemIdEdited = data.getStringExtra(KEY_ITEM_ID);

            adapter.updateItem(itemIdEdited, positionToEdit);
        } else {
            Toast.makeText(this, "Edit was cancelled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        ((ItemApplication)getApplication()).closeRealm();

        super.onDestroy();
    }
}
