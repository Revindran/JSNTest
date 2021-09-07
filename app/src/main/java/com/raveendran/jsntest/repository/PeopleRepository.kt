package com.raveendran.jsntest.repository

import com.raveendran.jsntest.api.Api
import com.raveendran.jsntest.db.PeopleDao
import com.raveendran.jsntest.model.People
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val dao: PeopleDao,
    private val api: Api
) {
    suspend fun getPeoples() = api.getAllPeoples()
    suspend fun insertPeople(people: People) = dao.upsert(people)
    fun getAllPeopleFromLocal() = dao.getAllPeoples()
    fun getAllDeletedPeoples() = dao.getAllDeletedPeoples()
    suspend fun deletePeople(people: People) = dao.delete(people)
    suspend fun updatePeople(people: People) = dao.update(people)
}