package com.ait.android.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ait.android.shoppinglist.adapter.ShoppingListRecyclerAdapter;
import com.ait.android.shoppinglist.data.Item;
import com.ait.android.shoppinglist.touch.ItemTouchHelperCallback;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by ellamary on 11/4/17.
 */

public class CreateNewItemActivity extends AppCompatActivity {

    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPrice)
    EditText etPrice;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.spinnerCategory)
    Spinner spinnerCategory;
    @BindView(R.id.cbPurchased)
    CheckBox cbPurchased;

    private ShoppingListRecyclerAdapter adapter;
    private Item itemToEdit = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_item);

        ButterKnife.bind(this);

        adapter = new ShoppingListRecyclerAdapter(this, ((ItemApplication)getApplication()).getRealmShoppingList());

        Spinner spinner = (Spinner) findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapterCat = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCat);

        if(getIntent().hasExtra(MainActivity.KEY_ITEM_ID)) {
            String itemId = getIntent().getStringExtra(MainActivity.KEY_ITEM_ID);

            itemToEdit = ((ItemApplication)getApplication()).getRealmShoppingList().where(Item.class).
                    equalTo("itemId", itemId).findFirst();
        }

        if(itemToEdit != null) {
            etName.setText(itemToEdit.getName());
            etPrice.setText(Double.toString(itemToEdit.getPrice()));
            etDescription.setText(itemToEdit.getDescription());
            int spinnerPosition = adapterCat.getPosition(itemToEdit.getCategory());
            spinnerCategory.setSelection(spinnerPosition);
            cbPurchased.setChecked(itemToEdit.isPurchased());
        }
    }

    @OnClick(R.id.btnAdd)
    public void addPressed(Button btnAdd) {

        String category = spinnerCategory.getSelectedItem().toString();
        boolean purchased = cbPurchased.isChecked();
        String name = "", description = "";
        double price = 0.00;

        if (!TextUtils.isEmpty(etName.getText().toString())) {
            name = etName.getText().toString();
        } else {
            etName.setError("This field cannot be empty");
        }

        if (!TextUtils.isEmpty(etPrice.getText().toString())) {
            price = Double.parseDouble(etPrice.getText().toString());
        } else {
            etPrice.setError("This field cannot be empty");
        }

        if (!TextUtils.isEmpty(etDescription.getText().toString())) {
            description = etDescription.getText().toString();
        } else {
            etDescription.setError("This field cannot be empty");
        }

        if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(etPrice.getText().toString()) &&
                !TextUtils.isEmpty(etDescription.getText().toString())){
            if(itemToEdit == null) {
                adapter.addItem(category, name, price, description, purchased);

                Intent intentHome = new Intent();
                intentHome.setClass(CreateNewItemActivity.this, MainActivity.class);
                startActivity(intentHome);
            } else {
                ((ItemApplication)getApplication()).getRealmShoppingList().beginTransaction();

                itemToEdit.setName(name);
                itemToEdit.setCategory(category);
                itemToEdit.setPurchased(purchased);
                itemToEdit.setPrice(price);

                ((ItemApplication)getApplication()).getRealmShoppingList().commitTransaction();

                Intent intentHome = new Intent();
                intentHome.putExtra(MainActivity.KEY_ITEM_ID, itemToEdit.getItemId());
                setResult(RESULT_OK, intentHome);

                finish();
            }
        }
    }


}
