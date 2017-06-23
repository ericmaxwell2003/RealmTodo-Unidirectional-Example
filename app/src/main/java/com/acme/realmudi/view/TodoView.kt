package com.acme.realmudi.view

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.acme.realmudi.R
import com.acme.realmudi.controller.addItem
import com.acme.realmudi.controller.deleteAllChecked
import com.acme.realmudi.controller.setAllCheckedValue
import com.acme.realmudi.controller.setSingleCheckedValue
import com.acme.realmudi.model.TodoItem
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import org.jetbrains.anko.find

class TodoView : AppCompatActivity() {

    val store: Realm by lazy { Realm.getDefaultInstance(); }

    val addTodoText by lazy { find<EditText>(R.id.add_todo_text) }
    val recyclerView by lazy { find<RecyclerView>(R.id.todo_list) }
    val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    val addItemFab by lazy { find<FloatingActionButton>(R.id.add_item_fab) }

    lateinit var model: RealmResults<TodoItem>

    /**
     * OnCreate we bind to the model.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        setSupportActionBar(toolbar)
        bindModel()
    }

    private fun bindModel() {

        model = store.where(TodoItem::class.java).findAllSortedAsync("createdDate", Sort.DESCENDING)
        model.addChangeListener { collection, changeSet ->
            if (changeSet?.insertions?.isNotEmpty() ?: false) {
                addTodoText.setText("")
                recyclerView.layoutManager.scrollToPosition(0)
            }
        }

        addItemFab.setOnClickListener { store.addItem(addTodoText.text.toString()) }

        recyclerView.adapter = TodoRecyclerViewAdapter(model, onValueChecked)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    val onValueChecked = { itemId: String, isSelected: Boolean -> store.setSingleCheckedValue(itemId, isSelected) }

    /**
     * OnDestroy we unbind from the model.
     */
    override fun onDestroy() {
        super.onDestroy()
        unbindModel()
    }

    private fun unbindModel() {
        model.removeAllChangeListeners()
        store.close()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_todo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var handled = true

        when (item.itemId) {
            R.id.action_check_all -> store.setAllCheckedValue(true)
            R.id.action_uncheck_all -> store.setAllCheckedValue(false)
            R.id.action_delete_checked -> store.deleteAllChecked()
            else -> handled = false
        }

        return handled
    }
}