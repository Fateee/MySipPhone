package com.sip.phone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.easycalltech.ecsdk.EcSipLib
import com.ec.utils.SipAudioManager
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.ui.view.DialView
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

//    private val PICK_CONTACT_REQUEST: Int = 666
//
//    private lateinit var homeViewModel: HomeViewModel
    private val scAudio: SipAudioManager = SipAudioManager.getInstance()
    private var ecsdk: EcSipLib? = null
    private var isCallOuting = false; //是否正在呼出
    //是否静音
    private var isMicOff = false
    //是否扩音器
    private var isVolumeOpen = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        pickContack?.setOnClickListener {
//            // 创建 Intent，指定联系人选择器的操作
//            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
//
//            // 启动联系人选择器 Activity，并等待用户选择联系人
//            activity?.startActivityForResult(intent, PICK_CONTACT_REQUEST)
//        }
//        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialView?.setDialViewListener(object : DialView.DialViewListener {
            override fun inputChange() {}

            override fun onCall(phone: String) {
                if (!isCallOuting) {
                    makeACall(phone)
                }
            }

            override fun onSetting() {}

            override fun onLongEvent(number: String?) {}
        })
    }

    private fun makeACall(phoneNumber: String) {
        scAudio.initialise(context)
        scAudio.muteMicrophone(isMicOff)
        scAudio.setSpeakerMode(isVolumeOpen)
        ecsdk = EcSipLib.getInstance(MainApplication.app)
        ecsdk?.makeCall(phoneNumber)
        isCallOuting = true;
    }

    fun setContactInfo(ret: Array<String?>) {
        dialView?.setContactInfo(ret)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
//            // 获取所选联系人的 URI
//            val contactUri: Uri? = data?.data
//            if (contactUri == null) {
//                Toast.makeText(context,"contactUri == null",Toast.LENGTH_SHORT).show()
//                return
//            }
//            // 查询所选联系人的信息
//            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
//            val cursor: Cursor? = context?.contentResolver?.query(contactUri, projection, null, null, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                // 获取联系人的电话号码
//                val phoneNumber: String = cursor.getString(index)
//                // 在这里可以将所选联系人的相关信息返回给调用方应用程序
//                text_home?.text = phoneNumber
//
//            }
//            cursor?.close()
//        }
//    }
}