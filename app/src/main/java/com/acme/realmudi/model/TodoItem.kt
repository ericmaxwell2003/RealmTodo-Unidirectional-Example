package com.acme.realmudi.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class TodoItem : RealmObject {

    @PrimaryKey
    @Required
    var id: String? = null

    @Required
    private var createdDate: Date? = null

    @Required
    var text: String? = null

    var isSelected: Boolean = false

    constructor(text: String) {
        this.id = UUID.randomUUID().toString()
        this.createdDate = Date()
        this.text = text
    }

    constructor() {}

}