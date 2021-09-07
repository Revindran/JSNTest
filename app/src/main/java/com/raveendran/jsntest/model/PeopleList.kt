package com.raveendran.jsntest.model

data class PeopleList(
    val data: MutableList<People>,
    val page: Int,
    val per_page: Int,
    val total: Int,
    val total_pages: Int
)