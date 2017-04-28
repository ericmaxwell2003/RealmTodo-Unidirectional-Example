package com.acme.realmcomponenttodo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class TodoView extends AppCompatActivity implements TodoRecyclerViewAdapter.ItemSelectionChangeDelegate {

    // The view has a dependency Component<TodoAction> to be used by this View to dispatch actions
    private Component<TodoAction> todoComponent;

    // It also has a reference to realm, which will be used to fetch the model, in this case TodoItems.
    private Realm realm;

    @BindView(R.id.add_todo_text) public EditText addTodoText;
    @BindView(R.id.todo_list)     public RecyclerView recyclerView;
    @BindView(R.id.add_item_fab)  public FloatingActionButton addItemFab;
    @BindView(R.id.toolbar)       public Toolbar toolbar;

    static Map<Integer, TodoAction.Type> menuResActionTypes = new HashMap<>();
    static {
        menuResActionTypes.put(R.id.action_check_all, TodoAction.Type.CHECK_ALL);
        menuResActionTypes.put(R.id.action_uncheck_all, TodoAction.Type.UNCHECK_ALL);
        menuResActionTypes.put(R.id.action_delete_checked, TodoAction.Type.DELETE_CHECKED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Instantiate a TodoComponent as the specific type of component for this View.
        // This could be abstracted to a dependency injection framework like Dagger or RoboGuice.
        realm = Realm.getDefaultInstance();
        todoComponent = new TodoComponent();

        recyclerView.setAdapter(new TodoRecyclerViewAdapter(this, fetchAllTodosAsync())); // Fetch the model, in this case TodoItems, and attach to the view.


        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeAllChangeListeners();
        if(realm.isInTransaction()) {
            realm.cancelTransaction();
        }
        realm.close();
    }

    @Override
    public void onSelectionChanged(String itemId, boolean isSelected) {
        TodoAction action =  isSelected ?
                new TodoAction(TodoAction.Type.CHECK_ITEM) :
                new TodoAction(TodoAction.Type.UNCHECK_ITEM);
        action.addInput("id", itemId);
        todoComponent.performAction(action);
    }

    @OnClick(R.id.add_item_fab)
    public void onAddItem() {
        final String taskText = addTodoText.getText().toString();
        if(!TextUtils.isEmpty(taskText)) {
            todoComponent.performAction(new TodoAction(TodoAction.Type.ADD_ITEM).addInput("text", taskText));
            addTodoText.setText("");
            recyclerView.getLayoutManager().scrollToPosition(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TodoAction.Type actionType = menuResActionTypes.get(item.getItemId());
        if(actionType != null) {
            todoComponent.performAction(new TodoAction(actionType));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private RealmResults<TodoItemModel> fetchAllTodosAsync() {
        return realm.where(TodoItemModel.class).findAllSortedAsync("createdDate", Sort.DESCENDING);
    }

}
