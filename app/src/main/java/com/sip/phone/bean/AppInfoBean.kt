package com.sip.phone.bean

class AppInfoBean {
    /**
     * code : -1
     * message : 参数错误
     * timestamp : 1680503558
     * signature : 6ac23a8d4ac31fb1717f18c170dda9d6
     * data : {"mobile":"手机号","channelId":"渠道","publicKey":"公钥","appName":"app名称","appVer":"app版本","installFile":"app下载地址","fileMd5":"app校验码"}
     */
    var code = 0
    var message: String? = null
    var timestamp = 0
    var signature: String? = null

    /**
     * mobile : 手机号
     * channelId : 渠道
     * publicKey : 公钥
     * appName : app名称
     * appVer : app版本
     * installFile : app下载地址
     * fileMd5 : app校验码
     */
    var data: DataBean? = null

    class DataBean {
        var channelId: String? = null
        var publicKey: String? = null
        var appName: String? = null
        var appVer: String? = null
        var installFile: String? = null
        var fileMd5: String? = null
    }
}