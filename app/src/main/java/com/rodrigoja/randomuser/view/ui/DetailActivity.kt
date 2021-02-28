package com.rodrigoja.randomuser.view.ui

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rodrigoja.randomuser.R
import com.rodrigoja.randomuser.data.database.UserEntity
import com.rodrigoja.randomuser.databinding.ActivityDetailBinding
import com.rodrigoja.randomuser.internal.USER
import com.rodrigoja.randomuser.model.User
import com.rodrigoja.randomuser.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity: AppCompatActivity() {
    private var user: User? = null
    private var userEntity: UserEntity? = null

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = intent.getSerializableExtra(USER) as User?
        val activityDetailBinding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        activityDetailBinding.user = user

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Profile"

        ivContactPhone.setOnClickListener {
            saveAsContact()
        }

        ivFavorite.setOnClickListener {
            if (userEntity == null){
                saveUser()
            }else{
                deleteUser()
            }
        }

        observeUserEntity()
        observeUserDelete()
        checkUser()
    }

    private fun saveAsContact(){
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            // Sets the MIME type to match the Contacts Provider
            type = ContactsContract.RawContacts.CONTENT_TYPE

            putExtra(ContactsContract.Intents.Insert.NAME, "${user?.name?.first} ${user?.name?.last}")

            putExtra(ContactsContract.Intents.Insert.PHONE, user?.phone)
            putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
        }
        startActivity(intent)
    }

    private fun observeUserEntity(){
        viewModel.userEntity.observe(this, {
            user ->
            userEntity = user
            if (user != null){
                ivFavorite.setImageResource(R.drawable.ic_favorite_selected)
            }else{
                ivFavorite.setImageResource(R.drawable.ic_favorite_unselected)
            }
        })
    }

    private fun observeUserDelete(){
        viewModel.deleteUser.observe(this, {
            if (it == 0){
                Toast.makeText(this, "Try again", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveUser(){
        user?.let {
            viewModel.insertUser(it)
        }
    }

    private fun deleteUser(){
        userEntity?.let {
            viewModel.deleteUser(it)
        }
    }

    private fun checkUser(){
        viewModel.getUser(user!!.email)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}