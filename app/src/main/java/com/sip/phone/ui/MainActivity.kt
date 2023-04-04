package com.sip.phone.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.easycalltech.ecsdk.EcSipNetworkUtil
import com.easycalltech.ecsdk.event.AccountRegisterEvent
import com.ec.sdk.EcphoneSdk
import com.ec.utils.MMKVUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.constant.Constants
import com.sip.phone.net.HttpPhone
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.base.BaseActivity
import com.sip.phone.ui.home.HomeFragment
import com.sip.phone.ui.login.LoginActivity
import com.sip.phone.update.UpdateDialog
import com.sip.phone.util.ContactUtil
import com.sip.phone.util.OverlayUtil
import com.sip.phone.util.ToastUtil

class MainActivity : BaseActivity() {
    private val TAG = "MainActivity_hy"
    companion object{
        fun startActivity(event: AccountRegisterEvent? = null) {
            val intent = Intent(MainApplication.app, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("callData", event)
//                intent.putExtra("staffNo", extNo)
            MainApplication.app?.startActivity(intent)
        }

        const val PICK_CONTACT_REQUEST: Int = 777
        const val DIAL_PHONE_REQUEST: Int = 555
    }
    private lateinit var navController : NavController

    override fun beforeSetContent(): Boolean {
        val mCallComingEvent : AccountRegisterEvent? = intent?.getParcelableExtra("callData")
        Log.i(TAG,"mCallComingEvent $mCallComingEvent")
        if (mCallComingEvent != null) return true
//        MMKVUtil.encode(Constants.PHONE,"")
        val phoneCached = MMKVUtil.decodeString(Constants.PHONE)
        val hasNumber = !phoneCached.isNullOrEmpty()
        Log.i(TAG,"phoneCached $phoneCached hasNumber $hasNumber")
        if (!hasNumber) {
            LoginActivity.startActivity()
            finish()
        } else {
            //直接执行登录流程
//            val registered = SdkUtil.isRegistered()
//            val noNet = EcSipNetworkUtil.getNetWorkState(MainApplication.app).equals(EcSipNetworkUtil.NETWORK_NONE)
//            Log.e(TAG,"how about network? noNet: $noNet")
//            if (!registered && !noNet) {//没注册成功且有网络时
//                SdkUtil.initAndBindLoginFlow(phoneCached!!)
//            }
        }
        return hasNumber
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initPages() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard))
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

        SdkUtil.checkMyPermissions(this) {
            ContactUtil.getAllContact()
        }
        OverlayUtil.initOverlayPermission(this)
        OverlayUtil.autoSelfLaunchPermission(this)
        checkAppVersion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            // 获取所选联系人的 URI
            val contactUri: Uri? = data?.data
            if (contactUri == null) {
                ToastUtil.showToast("抱歉，您选择的联系人没有电话，请重新选择！")
                return
            }
            val ret = getContacts(data)
            if (ret == null || ret[1].isNullOrEmpty()) {
                ToastUtil.showToast("抱歉，您选择的联系人没有电话，请重新选择！")
                return
            }
            if (navController.currentDestination != null) {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
                if (currentFragment is HomeFragment) {
                    currentFragment.setContactInfo(ret)
                }
            } else {
                ToastUtil.showToast("抱歉，出了点问题！")
            }
//            // 查询所选联系人的信息
//            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
//            val cursor: Cursor? = contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                // 获取联系人的电话号码
//                val phoneNumber: String = cursor.getString(index)
//                Log.i("huyi","phoneNumber --- $phoneNumber")
//                // 在这里可以将所选联系人的相关信息返回给调用方应用程序
////                textNum?.text = phoneNumber
//
//            }
//            cursor?.close()
        } else if (requestCode == DIAL_PHONE_REQUEST && resultCode == RESULT_OK) {
            // 获取用户输入的电话号码
            val phoneNumber = data?.getData()?.getSchemeSpecificPart();
            // 在这里可以将电话号码返回给调用方应用程序
            Log.i("huyi","phoneNumber == $phoneNumber")
        } else if (requestCode == Constants.REQUEST_CANCEL_ACCOUNT && resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
            LoginActivity.startActivity()
        }
    }



    private fun getContacts(data : Intent) : Array<String?>? {
        val contactUri = data.data ?: return null
        var name = "";
        var phoneNumber = "";
        val ret = arrayOfNulls<String>(2)
        val cursor = contentResolver.query(contactUri, null, null, null, null) ?: return null

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            ret[0] = name
//            var hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
//            if (hasPhone.equals("1")) {
//                hasPhone = "true";
//            } else {
//                hasPhone = "false";
//            }
//            if (hasPhone.equals("true")) {
                val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                    ?: return null
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                }
                phoneNumber = phoneNumber.replace("-", "").replace(" ", "")
                phones.close()
//            }
            cursor.close()
            ret[1] = phoneNumber
        }
        return ret
    }

    private fun checkAppVersion() {
        val phoneCached = MMKVUtil.decodeString(Constants.PHONE)
        HttpPhone.loginAndCheck(phoneCached,"") {
            when(it.code) {
                0,10 -> {
                    if (10 == it.code) {
                        UpdateDialog.show(this, it.data)
                    } else if (SdkUtil.channelId?.equals(it.data?.channelId) == false || SdkUtil.publicKey?.equals(it.data?.publicKey) == false) {
                        //如果使用中获取的配置数据与本地数据不同，则强制退出
                        ToastUtil.showToast("配置数据改变，退出登录")
                        SdkUtil.channelId = null
                        SdkUtil.publicKey = null
                        MMKVUtil.encode(Constants.PHONE,"")
                        EcphoneSdk.unRegister()
                        LoginActivity.startActivity()
                        finish()
                    }
                }
            }
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//
//
////        pickContact?.setOnClickListener {
////            // 创建 Intent，指定联系人选择器的操作
////            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
////
////            // 启动联系人选择器 Activity，并等待用户选择联系人
////            startActivityForResult(intent, PICK_CONTACT_REQUEST)
////        }
////        sendPhone?.setOnClickListener {
////            // 创建 Intent，指定拨号动作，并设置电话号码
////            // 创建 Intent，指定拨号动作，并设置电话号码
////            val phoneNumberUri = Uri.parse("tel:")
////            val dialIntent = Intent(Intent.ACTION_DIAL, phoneNumberUri)
////            // 启动拨号应用程序，并等待用户输入电话号码
////            startActivityForResult(dialIntent, DIAL_PHONE_REQUEST);
////        }
////        Toast.makeText(this,"测试一下toast大小如何",Toast.LENGTH_SHORT).show()
//    }
}