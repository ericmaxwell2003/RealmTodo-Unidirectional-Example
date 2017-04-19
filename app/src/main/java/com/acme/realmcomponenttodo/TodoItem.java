package com.acme.realmcomponenttodo;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TodoItem extends RealmObject {

    @PrimaryKey
    @Required
    private String id;

    @Required
    private Date createdDate;

    @Required
    private String text;

    private boolean selected;

    public TodoItem(String text) {
        this.id = UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.text = text;
    }

    // Getters / Setters

    public TodoItem() {}

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
}
