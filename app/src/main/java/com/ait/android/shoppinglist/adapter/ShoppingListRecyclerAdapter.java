package com.ait.android.shoppinglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ait.android.shoppinglist.MainActivity;
import com.ait.android.shoppinglist.R;
import com.ait.android.shoppinglist.data.Item;
import com.ait.android.shoppinglist.touch.ItemTouchHelperAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by ellamary on 11/3/17.
 */

public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Item> shoppingList;
    private double totalUnpurchased;

    private Realm realmShoppingList;
    private Context context;

    private PopupWindow popupDescription;

    public ShoppingListRecyclerAdapter(Context context, Realm realmShoppingList) {

        this.context = context;
        this.realmShoppingList = realmShoppingList;
        totalUnpurchased = 0;

        shoppingList = new ArrayList<Item>();

        RealmResults<Item> itemResult =
                realmShoppingList.where(Item.class).findAll().sort("name", Sort.ASCENDING);

        for (Item item : itemResult) {
            shoppingList.add(item);

            if (!item.isPurchased())
                totalUnpurchased += item.getPrice();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(final ShoppingListRecyclerAdapter.ViewHolder holder, final int position) {
        final Item itemData = shoppingList.get(position);
        final String description = itemData.getDescription();
        NumberFormat formatter = NumberFormat.getCurrencyInstance();


        holder.purchased.setChecked(itemData.isPurchased());
        holder.itemName.setText(itemData.getName());
        holder.itemCost.setText(formatter.format(itemData.getPrice()));

        LinearLayout itemLayout = holder.itemLayout;

        String category = itemData.getCategory();
        switch (category) {
            case "Book": holder.categoryImage.setImageResource(R.drawable.book);
                break;
            case "Clothes": holder.categoryImage.setImageResource(R.drawable.clothes);
                break;
            case "Food": holder.categoryImage.setImageResource(R.drawable.food);
                break;
            default:
                break;

        }

        holder.purchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realmShoppingList.beginTransaction();
                if(itemData.isPurchased())
                    totalUnpurchased += itemData.getPrice();
                else
                    totalUnpurchased -= itemData.getPrice();
                itemData.setPurchased(!itemData.isPurchased());

                ((MainActivity)context).setTotalText(totalUnpurchased);

                realmShoppingList.commitTransaction();
            }
        });

        holder.btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realmShoppingList.beginTransaction();
                itemData.deleteFromRealm();
                realmShoppingList.commitTransaction();

                shoppingList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).openEditActivity(
                        holder.getAdapterPosition(),
                        shoppingList.get(holder.getAdapterPosition()).getItemId()
                );
            }
        });

        holder.btnDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View descriptionView = LayoutInflater.from(context).inflate(R.layout.description_popup_layout, null);

                popupDescription = new PopupWindow(
                        descriptionView,
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                );

                TextView desc = descriptionView.findViewById(R.id.tvDescription);
                desc.setText(description);

                ImageButton closeButton = descriptionView.findViewById(R.id.ib_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupDescription.dismiss();
                    }
                });
                popupDescription.showAtLocation(descriptionView, Gravity.CENTER,0,0);
            }
        });


    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public double getTotalUnpurchased() { return totalUnpurchased; }

    @Override
    public void onItemDismiss(int position) {

        Item itemToDelete = shoppingList.get(position);
        realmShoppingList.beginTransaction();
        itemToDelete.deleteFromRealm();
        realmShoppingList.commitTransaction();

        shoppingList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(shoppingList, i, i+1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(shoppingList, i, i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void addItem(String category, String name, double price, String description, boolean purchased) {
        realmShoppingList.beginTransaction();

        Item newItem = realmShoppingList.createObject(Item.class, UUID.randomUUID().toString());
        newItem.setCategory(category);
        newItem.setName(name);
        newItem.setPrice(price);
        newItem.setPurchased(purchased);
        newItem.setDescription(description);

        if(!purchased)
            totalUnpurchased += price;

        realmShoppingList.commitTransaction();

        shoppingList.add(0, newItem);
        notifyDataSetChanged();

    }

    public void updateItem(String itemIdEdited, int positionToEdit) {
        Item item = realmShoppingList.where(Item.class).equalTo("itemId", itemIdEdited).findFirst();

        shoppingList.set(positionToEdit, item);

        notifyItemChanged(positionToEdit);

    }

    public void deleteAllItems() {
        realmShoppingList.beginTransaction();
        realmShoppingList.deleteAll();
        realmShoppingList.commitTransaction();

        shoppingList.clear();
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox purchased;
        private ImageView categoryImage;
        private TextView itemName, itemCost;
        private Button btnDescription;
        private ImageButton btnDeleteItem, btnEdit;
        private LinearLayout itemLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            purchased = itemView.findViewById(R.id.purchased);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemCost = itemView.findViewById(R.id.itemCost);
            btnDescription = itemView.findViewById(R.id.btnDescription);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            itemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }
}
