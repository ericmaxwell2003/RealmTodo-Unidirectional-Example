package com.acme.realmudi.view;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.acme.realmudi.R;
import com.acme.realmudi.model.TodoItem;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

class TodoRecyclerViewAdapter extends RealmRecyclerViewAdapter<TodoItem, TodoRecyclerViewAdapter.ViewHolder> {

    // Callback to TodoView (an ItemSelectionChangeDelegate).  Fires when an item is checked.
    interface ItemSelectionChangeDelegate {
        void onSelectionChanged(String itemId, boolean isSelected);
    }

    private ItemSelectionChangeDelegate itemSelectionChangeDelegate;

    TodoRecyclerViewAdapter(@NonNull  ItemSelectionChangeDelegate delegate, @NonNull OrderedRealmCollection<TodoItem> todoList) {
        super(todoList, true);
        itemSelectionChangeDelegate = delegate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        OrderedRealmCollection data = getData();
        if(data != null) {
            holder.checkBox.setOnCheckedChangeListener(null);
            final TodoItem item = getData().get(position);
            holder.itemId = item.getId();

            TextView tv = holder.titleView;
            tv.setText(item.getText());
            if(item.isSelected()) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }
            holder.checkBox.setChecked(item.isSelected());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean newCheckedState) {
                    if(item.isValid() && item.isSelected() != newCheckedState) {
                        itemSelectionChangeDelegate.onSelectionChanged(item.getId(), newCheckedState );
                    }
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        String itemId;
        final TextView titleView;
        final CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.todo_item_checkbox);
            titleView = (TextView) view.findViewById(R.id.todo_item_text);
        }
    }
}
