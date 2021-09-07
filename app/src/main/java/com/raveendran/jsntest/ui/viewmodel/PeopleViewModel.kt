package com.raveendran.jsntest.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raveendran.jsntest.model.People
import com.raveendran.jsntest.model.PeopleList
import com.raveendran.jsntest.repository.PeopleRepository
import com.raveendran.jsntest.util.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val repository: PeopleRepository,
    private val hasInternetConnectionState: Boolean
) : ViewModel() {

    val allPeoples: MutableLiveData<NetworkResponse<PeopleList>> = MutableLiveData()
    var allPeoplesResponse: PeopleList? = null


    fun getAllPeopleDetailsFromLocal(): LiveData<List<People>> {
        return repository.getAllPeopleFromLocal()
    }

    fun getAllDeletedPeoplesFromLocal(): LiveData<List<People>> {
        return repository.getAllDeletedPeoples()
    }

    suspend fun deletePeople(people: People) = repository.deletePeople(people)

    suspend fun insertPeople(people: People) = repository.insertPeople(people)

    suspend fun updatePeople(people: People) = repository.updatePeople(people)

    suspend fun getAllPeopleSafe() {
        allPeoples.postValue(NetworkResponse.Loading())
        if (hasInternetConnectionState) {
            try {
                val response = repository.getPeoples()
                allPeoples.postValue(handlePeoplesResponse(response))
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> allPeoples.postValue(NetworkResponse.Error("Network Failure"))
                    else -> allPeoples.postValue(NetworkResponse.Error("Conversion Error"))
                }
            }
        } else {
            allPeoples.postValue(NetworkResponse.Error("No internet connection"))
        }
    }

    private suspend fun handlePeoplesResponse(response: Response<PeopleList>): NetworkResponse<PeopleList> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                allPeoplesResponse = resultResponse
                allPeoplesResponse?.let { list ->
                    list.data.forEach {
                        repository.insertPeople(it)
                    }
                }
                return NetworkResponse.Success(allPeoplesResponse ?: resultResponse)
            }
        }
        return NetworkResponse.Error(response.message())
    }

}