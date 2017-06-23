package com.acme.realmudi.controller

import android.text.TextUtils
import com.acme.realmudi.model.TodoItem
import io.realm.Realm
import org.jetbrains.anko.doAsync


fun Realm.addItem(text: String) {

    if (TextUtils.isEmpty(text)) {
        return
    }

    executeTransactionAsync(Realm.Transaction { bgRealm -> bgRealm.copyToRealmOrUpdate(TodoItem(text)) })
}

fun Realm.deleteAllChecked() {
    executeTransactionAsync(Realm.Transaction { bgRealm -> bgRealm.where(TodoItem::class.java).equalTo("selected", true).findAll().deleteAllFromRealm() })
}

fun Realm.setAllCheckedValue(isChecked: Boolean) {
    executeTransactionAsync(Realm.Transaction { bgRealm ->
        val items = bgRealm.where(TodoItem::class.java).findAll()
        for (item in items) {
            item.isSelected = isChecked
        }
    })
}


fun Realm.setSingleCheckedValue(itemId: String, isChecked: Boolean) {

    if (TextUtils.isEmpty(itemId)) {
        return
    }

    executeTransactionAsync(Realm.Transaction { bgRealm ->
        val TodoItem = bgRealm.where(TodoItem::class.java).equalTo("id", itemId).findFirst()
        TodoItem.isSelected = isChecked
    })
}

fun Realm.inBackroundTx(operations: List<((bgRealm: Realm)-> Unit)>) {

    doAsync {

        val bgRealm = Realm.getDefaultInstance()

        try {
            bgRealm.beginTransaction()
            operations.forEach { it.invoke(bgRealm) }
            bgRealm.commitTransaction()

        } catch (exception: Exception) {
            bgRealm.cancelTransaction()

        } finally {
            if(!bgRealm.isClosed) {
                bgRealm.close()
            }
        }
    }

}