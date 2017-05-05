package com.acme.realmudi.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.acme.realmudi.R;
import com.acme.realmudi.controller.TodoController;
import com.acme.realmudi.model.TodoItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class TodoView extends AppCompatActivity implements TodoRecyclerViewAdapter.ItemSelectionChangeDelegate {

    private Realm realm;

    @BindView(R.id.add_todo_text) public EditText addTodoText;
    @BindView(R.id.todo_list)     public RecyclerView recyclerView;
    @BindView(R.id.add_item_fab)  public FloatingActionButton addItemFab;
    @BindView(R.id.toolbar)       public Toolbar toolbar;

    private TodoController controller;
    private RealmResults<TodoItem> model;

    /**
     * OnCreate we setup our view and bind to the model.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        bindController();
        bindModel();
    }

    private void bindController() {
        controller = new TodoController();
    }

    private void bindModel() {
        realm = Realm.getDefaultInstance();
        model = realm.where(TodoItem.class).findAllSortedAsync("createdDate", Sort.DESCENDING);
        recyclerView.setAdapter(new TodoRecyclerViewAdapter(this, model));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        model.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<TodoItem>>() {
            @Override
            public void onChange(RealmResults<TodoItem> collection, OrderedCollectionChangeSet changeSet) {
                if (changeSet != null && changeSet.getInsertions().length >= 1) {
                    addTodoText.setText("");
                    recyclerView.getLayoutManager().scrollToPosition(0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }


    /**
     * OnDestroy we unbind from the model.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindModel();
        unbindController();
    }

    private void unbindController() {
        controller.close();
    }

    private void unbindModel() {
        model.removeAllChangeListeners();
        realm.close();
    }


    /**
     *  OnSelectionChanged, onAddItem(), onOptionsItemSelected()
     *  Bind UI Events to the controller.
     */
    @Override
    public void onSelectionChanged(String itemId, boolean isSelected) {
        controller.setSingleCheckedValue(itemId, isSelected);
    }

    @OnClick(R.id.add_item_fab)
    public void onAddItem() {
        controller.addItem(addTodoText.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean handled = true;

        switch (item.getItemId()) {
            case R.id.action_check_all:
                controller.setAllCheckedValue(true);
                break;
            case R.id.action_uncheck_all:
                controller.setAllCheckedValue(false);
                break;
            case R.id.action_delete_checked:
                controller.deleteAllChecked();
                break;
            default:
                handled = false;
                break;
        }

        return handled;
    }

}
