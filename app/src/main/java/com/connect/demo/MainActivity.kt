package com.connect.demo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.getphyllo.ConnectCallback
import com.getphyllo.PhylloConnect
import com.getphyllo.utils.LogUtils
import com.google.gson.Gson


// Should not change the value of REDIRECT_URI
const val REDIRECT_URI = "com.getphyllo://auth"
const val YOUTUBE_PLATFORM_ID = "14d9ddf5-51c6-415e-bde6-f8ed36ad7054"
const val INSTAGRAM_PLATFORM_ID = "9bb8913b-ddd9-430b-a66a-d74d846e6c66"
const val USER_NAME_PREFIX = "phyllo_"
const val PREF_NAME = "phyllo_Pref"
const val KEY_USER_ID = "UserID"
const val CONFIG_JSON_FILE = "config.json"

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = LogUtils.makeLogTag(MainActivity::class.java)
    }

    private var mTestDialog: ConnectDialog? = null
    private var mDialog: ProgressDialog? = null
    private var mUserAvailCheck: AppCompatCheckBox? = null
    private var mConnectPlatformsView: AppCompatButton? = null
    private var mLaunchInstBtn: AppCompatButton? = null
    private var mLaunchYoutubeBtn: AppCompatButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ConfigProvider.saveConfig(readConfigFromJson())
        mConnectPlatformsView = findViewById(R.id.create_new)
        mUserAvailCheck = findViewById(R.id.user_availability_check)
        mLaunchInstBtn = findViewById(R.id.launch_instagram)
        mLaunchYoutubeBtn = findViewById(R.id.launch_youtube)

        mConnectPlatformsView?.setOnClickListener {

            if (mUserAvailCheck?.isChecked == true && !TextUtils.isEmpty(getUserId())) {
                showProgressBar()
                generateToken(getUserId()!!, "")
            } else {
                val user = generateNewUser()
                createNewUser(user.first, user.second)
            }

        }
        mLaunchInstBtn?.setOnClickListener {
            if (mUserAvailCheck?.isChecked == true && !TextUtils.isEmpty(getUserId())) {
                showProgressBar()
                generateToken(getUserId()!!, INSTAGRAM_PLATFORM_ID)
            } else {
                val user = generateNewUser()
                createNewUser(user.first, user.second, INSTAGRAM_PLATFORM_ID)
            }
        }

        mLaunchYoutubeBtn?.setOnClickListener {
            if (mUserAvailCheck?.isChecked == true && !TextUtils.isEmpty(getUserId())) {
                showProgressBar()
                generateToken(getUserId()!!, YOUTUBE_PLATFORM_ID)
            } else {
                val user = generateNewUser()
                createNewUser(user.first, user.second, YOUTUBE_PLATFORM_ID)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        availabilityViewUpdate()
    }

    private fun availabilityViewUpdate() {
        if (TextUtils.isEmpty(getUserId())) {
            mUserAvailCheck?.visibility = View.GONE
        } else {
            mUserAvailCheck?.visibility = View.VISIBLE
        }
    }

    private fun initSDK(userId: String, token: String, platformId: String? = "") {
        var callback = object : ConnectCallback() {

            override fun onAccountConnected(
                account_id: String?,
                work_platform_id: String?,
                user_id: String?
            ) {
                Log.d(TAG, "onAccountConnected $account_id $work_platform_id  $user_id")
                showTestDialog(
                    "onAccountConnected",
                    "account_id : $account_id work_platform_id : $work_platform_id  user_id : $user_id"
                )
            }

            override fun onAccountDisconnected(
                account_id: String?,
                work_platform_id: String?,
                user_id: String?
            ) {
                Log.d(TAG, "onAccountDisconnected $account_id $work_platform_id  $user_id")
                showTestDialog(
                    "onAccountDisconnected",
                    "account_id : $account_id work_platform_id : $work_platform_id  user_id : $user_id"
                )
            }

            override fun onTokenExpired(user_id: String?) {
                Log.d(TAG, "onTokenExpired  $user_id")
                showTestDialog("onTokenExpired", " user_id : $user_id")
            }

            override fun onExit(reason: String?, user_id: String?) {
                Log.d(TAG, "onExit $user_id $reason")
                showTestDialog("onExit", " user_id : $user_id")
            }

            override fun onConnectionFailure(
                reason: String?,
                work_platform_id: String?,
                user_id: String?
            ) {
                showTestDialog(
                    "onConnectionFailure",
                    " reason : $reason work_platform_id : $work_platform_id user_id : $user_id"
                )
            }
        }
        var map=hashMapOf<String,Any?>(
            "clientDisplayName" to "Phyllo Connect",
            "token" to token,
            "workPlatformId" to platformId,
            "userId" to userId,
            "environment" to ConfigProvider.getEnvironment(),
            "callback" to callback,
            "singleAccount" to true
        )
        PhylloConnect.initialize(context = this@MainActivity, map)

        PhylloConnect.open()

        LogUtils.LOGD("Version Details", PhylloConnect.Version().toString())

    }

    private fun createNewUser(usr: String, extId: String, platformId: String = "") {
        showProgressBar()
        ApiServiceProvider.createUser(usr, extId, object : ApiServiceProvider.UserResponseCallback {
            override fun onUserResponse(response: UserResponse?) {
                if (response != null) {
                    saveUserId(response.id)
                    generateToken(response.id, platformId)
                }
            }
        })
    }

    private fun generateToken(usrId: String, platformId: String = "") {
        ApiServiceProvider.generateToken(usrId,
            REDIRECT_URI,
            object : ApiServiceProvider.TokenResponseCallback {
                override fun onTokenResponse(response: TokenResponse?) {
                    if (response != null) {
                        initSDK(usrId, response.sdk_token, platformId)
                    }
                    hideProgressBar()
                }
            })
    }

    private fun showProgressBar() {
        showProgressBar(desc = "", disableCancel = false)
    }

    private fun showProgressBar(desc: String?, disableCancel: Boolean) {
        hideProgressBar()
        mDialog = ProgressDialog(this)
        mDialog?.setCancelable(!disableCancel)
        mDialog?.showDialog(desc)
    }

    fun hideProgressBar() {
        if (mDialog != null && mDialog?.isShowing!!) {
            mDialog?.dismiss()
        }
    }

    private fun saveUserId(userId: String?) {
        val sharedPref = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val sharedPrefEdit = sharedPref.edit()
        sharedPrefEdit.putString(KEY_USER_ID, userId)
        sharedPrefEdit.apply()
    }

    private fun getUserId(): String? {
        val sharedPref = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        return sharedPref.getString(KEY_USER_ID, null)
    }

    private fun generateNewUser(): Pair<String, String> {
        val milli = System.currentTimeMillis()
        val username = "$USER_NAME_PREFIX$milli"
        val externalId = "$milli"

        return Pair(username, externalId)
    }

    private fun readConfigFromJson(): Config? {
        val jsonString = application.assets.open(CONFIG_JSON_FILE).bufferedReader().use {
            it.readText()
        }
        return Gson().fromJson(jsonString, Config::class.java)
    }
    private fun showTestDialog(title: String?, desc: String?) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this@MainActivity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1234)
            } else {
                mTestDialog = ConnectDialog(this@MainActivity)
                mTestDialog?.showDialog(title, desc)
            }
        } else {
            mTestDialog = ConnectDialog(this@MainActivity)
            mTestDialog?.showDialog(title, desc)
        }
    }

}