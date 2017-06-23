package com.acme.realmudi.model

import com.acme.realmudi.controller.inBackroundTx
import io.realm.Realm
import io.realm.RealmModel
import org.jetbrains.anko.doAsync

class TodoStore {

    fun addItem(text: String) {
        if (text.isNotEmpty()) {
            inBackroundTx { r -> r.copyToRealmOrUpdate(TodoItem(text)) }
        }
    }

    fun TodoItem.deleteAllChecked() {
        inBackroundTx { r -> r.where(TodoItem::class.java).equalTo("selected", true).findAll().deleteAllFromRealm() }
    }

    fun TodoItem.setAllCheckedValue(isChecked: Boolean) {
        inBackroundTx { r -> r.where(TodoItem::class.java).findAll().forEach { it.isSelected = isChecked } }
    }

    fun TodoItem.setSingleCheckedValue(itemId: String, isChecked: Boolean) {
        if (itemId.isNotEmpty()) {
            inBackroundTx { r -> r.where(TodoItem::class.java).equalTo("id", itemId).findFirst()?.isSelected = isChecked }
        }
    }

    fun inBackroundTx(operation: ((bgRealm: Realm)-> Unit)) {

        doAsync {

            val bgRealm = Realm.getDefaultInstance()

            try {
                bgRealm.beginTransaction()
                operation.invoke(bgRealm)
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
}

