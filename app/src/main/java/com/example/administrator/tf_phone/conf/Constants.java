package com.example.administrator.tf_phone.conf;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-13 14:11
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class Constants {
    public static String TELEPHONE_NUMBER ="TELEPHONE_NUMBER";
    public static String TELEPHONE_STATE ="TELEPHONE_STATE";
    //打电话
    public static String ACTION_NEW_OUTGOING_CALL ="ACTION_NEW_OUTGOING_CALL";
    //打电话
    public static String ACTION_ANSWER_CALL ="ACTION_ANSWER_CALL";
    //响铃
    public static String CALL_STATE_RINGING ="CALL_STATE_RINGING";
    //接听电话
    public static boolean CALL_STATE_OFFHOOK =false;
    //是否是拨打电话
    public static boolean isOutCall =false;
    //是否有来电
    public static boolean isInCall =false;
    //判断去电页面是否可以销毁了
    public static boolean isOutCallFinish =false;
    //是否是拨打服务号
    public static boolean isOutCall10086 =false;
    //是否是拨打电话
    public static boolean isOnClickItemOutCall =false;
    //是否有修改通讯录的资料,同步到通话记录里面
    public static boolean isUpdateContacts =false;
    //是否是主页新建进来的
    public static boolean isMainAddContacts =false;
    //申请弹窗权限
    public static String PERMISSION_DRAW_OVERLAYS = "PERMISSION_DRAW_OVERLAYS";

}
