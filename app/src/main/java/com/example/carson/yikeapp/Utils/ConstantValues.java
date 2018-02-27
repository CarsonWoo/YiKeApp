package com.example.carson.yikeapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 84594 on 2018/1/17.
 */

public class ConstantValues {
    private static final String TAG = "ConstantValues";

    public static final String APP_ID="com.example.carson.yikeapp";

    public static final String URL_PHONE_REQUEST = "https://www.yiluzou.cn/yike/public/index.php/registerSend";

    public static final String URL_LOGIN = "https://www.yiluzou.cn/yike/public/index.php/login";

    public static final String URL_REGISTER = "https://www.yiluzou.cn/yike/public/index.php/register";

    public static final String URL_CHANGE_PASSWORD = "https://www.yiluzou.cn/yike/public/index.php/changepassword";

    public static final String URL_CHANGE_PHONE_REQUEST = "https://www.yiluzou.cn/yike/public/index.php/changeSend";

    public static final String URL_FILL_USER_INFO = "https://www.yiluzou.cn/yike/public/index.php/filluserinfo";

    public static final String URL_FILL_HOTEL_INFO = "https://www.yiluzou.cn/yike/public/index.php/fillhotelinfo";

    public static final String URL_CHANGE_ICON = "https://www.yiluzou.cn/yike/public/index.php/change_icon";

    public static final String URL_GET_USER_INFO = "https://www.yiluzou.cn/yike/public/index.php/getinfo";

    public static final String URL_CHANGE_PWD_BY_OLD_PWD = "https://www.yiluzou.cn/yike/public/index.php/changepwd";

    public static final int CODE_TAKE_PHOTO = 1;

    public static final int TYPE_TAKE_PHOTO = 1;

    public static final int CODE_PICK_PHOTO = 2;

    public static final int REQUESTCODE_START_SETTING = 10001;

    public static final int RESULTCODE_SETTING_ACCOUNT_QUIT = 10002;

    public static final String USER_TYPE_STORE = "店家";

    public static final String USER_TYPE_NORMAL = "用户";

    //获取用户信息时服务器返回json数据key名
    //义工
    public static final String KEY_USER_REALNAME = "realname";
    public static final String KEY_USER_TYPE = "usertype";
    public static final String KEY_USER_GENDER = "gender";
    public static final String KEY_USER_IDCARD = "idcard";
    public static final String KEY_USER_BIRTH = "birth";
    public static final String KEY_USER_AREA = "area";
    public static final String KEY_USER_INTRO = "introduction";
    public static final String KEY_USER_PHOTO_URL = "photo_url";
    public static final String KEY_USER_EXP = "experience";
    public static final String KEY_USER_DIA_NUM = "diary_number";
    public static final String KEY_USER_BALACE = "all_pay";
    public static final String KEY_USER_RESERVE = "online_booking";
    public static final String KEY_USER_EXP_POST = "experience_number";
    public static final String KEY_USER_CREDIT = "credit_score";
    //店家
    public static final String KEY_STORE_REALNAME = "realname";
    public static final String KEY_STORE_TYPE = "usertype";
    public static final String KEY_STORE_GENDER = "gender";
    public static final String KEY_STORE_IDCARD = "idcard";
    public static final String KEY_STORE_BIRTH = "birth";
    public static final String KEY_STORE_AREA = "area";
    public static final String KEY_STORE_INTRO = "introduction";
    public static final String KEY_STORE_PHOTO_URL = "photo_url";
    public static final String KEY_STORE_EXP = "experience";
    public static final String KEY_STORE_ATY_NUM = "activity_number";
    public static final String KEY_STORE_CREDIT = "credit_score";

    public static final String KEY_TOKEN = "token";
    public static final String KEY_STORE_NAME = "storeName";
    public static final String KEY_PHONE_NUM = "phone";
    public static final String KEY_PSW = "password";


    //取得token
    public static String getCachedToken(Context context){
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_TOKEN,null);
    }

    //储存token
    public static void cachToken(Context context,String token){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_TOKEN,token);
        editor.apply();
    }

    //清除token（退出登录用）
    public static void removeToken(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    //获取用户类型
    public static String getCachedUserType(Context context){
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_USER_TYPE,null);
    }

    //储存用户类型
    public static void cachUserType(Context context,String userType){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USER_TYPE,userType);
        editor.apply();
    }

    //获取密码
    public static String getCachedPsw(Context context){
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_PSW,null);
    }

    //储存密码
    public static void cachPsw(Context context,String psw){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_PSW,psw);
        editor.apply();
    }
}
