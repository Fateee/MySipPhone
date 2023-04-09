package com.sip.phone.bean

class LocateBean {
    var code = 0
    var message: String? = null
    var timestamp = 0
    var signature: String? = null

    var data: DataBean? = null

    class DataBean {
        var phone: String? = null
        var location: String? = null
        var carrier: String? = null
    }
}