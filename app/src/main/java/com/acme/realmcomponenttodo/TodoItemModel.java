package com.acme.realmcomponenttodo;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TodoItemModel extends RealmObject {

    @PrimaryKey
    @Required
    private String id;

    @Required
    private Date createdDate;

    @Required
    private String text;

    private boolean selected;

    public TodoItemModel(String text) {
        this.id = UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.text = text;
    }

    // Getters / Setters

    public TodoItemModel() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static void addItem(final String text) {

        if(text == null) {
            return;
        }

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    bgRealm.copyToRealmOrUpdate(new TodoItemModel(text));
                }
            });
        }
    }

    public static void deleteAllChecked() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    bgRealm.where(TodoItemModel.class).equalTo("selected", true).findAll().deleteAllFromRealm();
                }
            });
        }
    }

    public static void setAllCheckedValue(final boolean isChecked) {
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    RealmResults<TodoItemModel> items = bgRealm.where(TodoItemModel.class).findAll();
                    for(TodoItemModel item : items) { item.setSelected(isChecked); }
                }
            });
        }
    }

    public static void setSingleCheckedValue(final String itemId, final boolean isChecked) {

        if(itemId == null) {
            return;
        }

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    TodoItemModel todoItemModel = bgRealm.where(TodoItemModel.class).equalTo("id", itemId).findFirst();
                    todoItemModel.setSelected(isChecked);
                }
            });
        }
    }
}
