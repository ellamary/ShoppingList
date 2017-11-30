package com.ait.android.shoppinglist.touch;

/**
 * Created by ellamary on 11/3/17.
 */

public interface ItemTouchHelperAdapter {

    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);
}
