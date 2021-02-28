package com.rodrigoja.randomuser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rodrigoja.randomuser.data.database.UserEntity
import com.rodrigoja.randomuser.data.database.toUserList
import com.rodrigoja.randomuser.di.DaggerAppComponent
import com.rodrigoja.randomuser.model.User
import com.rodrigoja.randomuser.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserViewModel: ViewModel() {
    @Inject
    lateinit var repository: UserRepository

    private var page: Int = 1

    private val compositeDisposable by lazy { CompositeDisposable() }

    private val _users by lazy { MutableLiveData<List<User>>() }
    val users: LiveData<List<User>>
        get() = _users

    private val _isInProgress by lazy { MutableLiveData<Boolean>() }
    val isInProgress: LiveData<Boolean>
        get() = _isInProgress

    private val _isError by lazy { MutableLiveData<Boolean>() }
    val isError: LiveData<Boolean>
        get() = _isError

    private val _userEntity by lazy { MutableLiveData<UserEntity?>() }
    val userEntity: LiveData<UserEntity?>
        get() = _userEntity

    private val _favorites by lazy { MutableLiveData<List<User>>() }
    val favorites: LiveData<List<User>>
        get() = _favorites

    private val _deleteUser by lazy { MutableLiveData<Int>() }
    val deleteUser: LiveData<Int>
        get() = _deleteUser

    init {
        DaggerAppComponent.create().inject(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onGetUsers(){
        compositeDisposable.add(
            repository.getUsers(page, 50)
                .debounce(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    usersList ->
                    _isInProgress.postValue(true)
                    if (usersList?.results != null && usersList.results.isNotEmpty()){
                        _isError.postValue(false)
                        _users.postValue(usersList.results)
                    }
                    _isInProgress.postValue(false)
                }, {
                    throwable ->
                    _isInProgress.postValue(true)
                    _isError.postValue(true)
                    _isInProgress.postValue(false)

                })
        )
        page++
    }

    fun getFavorites(){
        compositeDisposable.add(
                repository.getFavorites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            favoriteList ->
                            if (favoriteList != null && favoriteList.isNotEmpty()){
                                _favorites.postValue(favoriteList.toUserList())
                            }else{
                                _favorites.postValue(emptyList())
                            }
                        }, {
                            throwable ->
                                _favorites.postValue(emptyList())
                        })
        )
    }

    fun insertUser(user: User){
        compositeDisposable.add(
                repository.insertUser(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            id ->
                            if (id > 0){
                                getUser("${user.id.name}-${user.id.value}")
                            }else{
                                _userEntity.postValue(null)
                            }
                        },{
                            _userEntity.postValue(null)
                        })
        )
    }

    fun getUser(userId: String){
        compositeDisposable.add(
                repository.getUser(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            userList ->
                            if (userList != null && userList.isNotEmpty()){
                                _userEntity.postValue(userList[0])
                            }else{
                                _userEntity.postValue(null)
                            }
                        }, {
                            throwable ->
                            _userEntity.postValue(null)
                        })
        )
    }

    fun deleteUser(user: UserEntity){
        compositeDisposable.add(
                repository.deleteUser(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            id ->
                            if (id > 0){
                                _userEntity.postValue(null)
                            }else{
                                _deleteUser.postValue(0)
                            }
                        },{
                            _deleteUser.postValue(0)
                        })
        )
    }
}