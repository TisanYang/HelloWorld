package hf.car.wifi.video.constant

object ApiConstant {

    const val SOCKET_IP: String = "192.168.0.1"

    const val SOCKET_PORT: Int = 4342

    /**
     * 获取系统信息
     */
    const val SYS_INFO: Short = 0x0000

    /**
     * 获取设备状态
     */
    const val DEVICE_STATE: Short = 0x0001

    /**
     * 获取设备信息
     */
    const val DEVICE_INFO: Short = 0x0002

    /**
     * 设置设备信息
     */
    const val SETTING_DEVICE_INFO: Short = 0x0003

    /**
     * 获取设备simid
     */
    const val DEVICE_SIMID: Short = 0x0004

    /**
     * 开启实时视频
     */
    const val OPEN_REAL_VIDEO: Short = 0x0601

    /**
     * 关闭实时视频
     */
    const val CLOSE_REAL_VIDEO: Short = 0x0602

    /**
     * 获取当前设备人脸数据
     */
    const val DEVICE_FACE: Short = 0x0088

    /**
     * 设置人脸数据
     */
    const val SETTING_DEVICE_FACE: Short = 0x0089

    /**
     * 删除人脸数据
     */
    const val DELETE_DEVICE_FACE: Short = 0x008a

    /**
     * 获取时间参数
     */
    const val GET_DEVICE_TIME: Short = 0x0006

    /**
     * 同步时间
     */

    const val SET_DEVICE_TIM: Short = 0x0007

    /**
     * 获取盲区画线标定区域
     */
    const val GET_BSD: Short = 0x091E

    /**
     * 获取录像数据
     */
    const val GET_REPLY_DATA: Short = 0x0405

    /**
     * 播放
     */
    const val START_REPLY_DATA: Short = 0x0621


    /**
     * 回放控制
     */
    const val CONTROL_PLAY: Short = 0x0624

    /**
     * 停止播放
     */
    const val STOP_PLAY: Short = 0x0622

    /**
     * 设备升级
     */
    const val UPGRADE: Short = 0x0008

    /**
     * 测试信息
     */
    const val TEST_INFO: Short = 0x0013

    /**
     * 下发预升级
     */
    const val FILE_UPGRADE: Short = 0x000A

    /**
     * 发送升级文件
     */
    const val FILE_SEND_PACKAGE: Short = 0x000B

    /**
     * 获取BSD标定点
     */
    const val GET_BSD_ARR: Short = 0x091E

    /**
     * 设置BSD标定点
     */
    const val SET_BSD_ARR: Short = 0x091F

    /**
     * 主动安全参数获取
     */
    const val GET_DMS_DATA: Short = 0x0082

    /**
     * 主动安全参数获取
     */
    const val SET_DMS_DATA: Short = 0x0083

    /**
     *  获取盲区锁车失效时长
     */
    const val GET_BLAND_TIME: Short = 0x0957

  /**
     *  设置盲区锁车失效时长
     */
    const val SET_BLAND_TIME: Short = 0x0958




}