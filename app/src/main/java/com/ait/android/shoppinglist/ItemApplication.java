package com.ait.android.shoppinglist;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ellamary on 11/4/17.
 */

public class ItemApplication extends Application {

    private Realm realmShoppingList;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realmShoppingList = Realm.getInstance(config);
    }

    public void closeRealm() {
        realmShoppingList.close();
    }

    public Realm getRealmShoppingList() {
        return realmShoppingList;
    }
}
