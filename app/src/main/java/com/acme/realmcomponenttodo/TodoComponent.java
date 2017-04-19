package com.acme.realmcomponenttodo;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;

public class TodoComponent implements Component<TodoAction> {

    private static final String TAG = TodoComponent.class.getName();

    @Override
    public void performAction(TodoAction action) {

        Log.i(TAG, "Request Perform Action: " + action.getType().toString());

        // If this list gets to large, we can always break our TodoComponent into smaller, compose-able, components.
        switch (action.getType()) {
            case ADD_ITEM:
                addItem(action.getInput("text").toString());
                break;

            case CHECK_ITEM:
                setSingleCheckedValue(action.getInput("id").toString(), true);
                break;

            case UNCHECK_ITEM:
                setSingleCheckedValue(action.getInput("id").toString(), false);
                break;

            case CHECK_ALL:
                setAllCheckedValue(true);
                break;

            case UNCHECK_ALL:
                setAllCheckedValue(false);
                break;

            case DELETE_CHECKED:
                deleteAllChecked();
                break;

            default:
                Log.w(TAG, "Action not supported");
        }

    }

    // Private implementation to support the available actions for this TodoComponent.

    private void addItem(final String text) {

        if(text == null) {
            return;
        }

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    bgRealm.copyToRealmOrUpdate(new TodoItem(text));
                }
            });
        }
    }

    private void deleteAllChecked() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    bgRealm.where(TodoItem.class).equalTo("selected", true).findAll().deleteAllFromRealm();
                }
            });
        }
    }

    private void setAllCheckedValue(final boolean isChecked) {
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    RealmResults<TodoItem> items = bgRealm.where(TodoItem.class).findAll();
                    for(TodoItem item : items) { item.setSelected(isChecked); }
                }
            });
        }
    }

    private void setSingleCheckedValue(final String itemId, final boolean isChecked) {

        if(itemId == null) {
            return;
        }

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    TodoItem todoItem = bgRealm.where(TodoItem.class).equalTo("id", itemId).findFirst();
                    todoItem.setSelected(isChecked);
                }
            });
        }
    }

}
