package com.commonsware.todo

class ToDoRepository {
    var items = listOf(
        ToDoModel(
            description = "Buy a copy of _Exploring Android_",
            isCompleted = true,
            notes = "See https://www.google.com"
        ),
        ToDoModel(
            description = "Buy a copy of  Android_",
            isCompleted = true,
            notes = "See https://wares.commonsware.com"
        ),
        ToDoModel(
            description = "Buy an Android_",
            isCompleted = true,
            notes = "See https://wares.commonsware.com"
        )

    )
}