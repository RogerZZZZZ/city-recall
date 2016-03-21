package com.example.rogerzzzz.cityrecall.utils;

import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;

/**
 * Created by rogerzzzz on 16/3/21.
 */
public class UserUtils {
    public static int registFlag = -1;

    public static void initCloudService(Context context){
        AVOSCloud.initialize(context, "vbq8HEM7nninw4GP4K3IkNiW-gzGzoHsz", "WruY52lpBmOthvNASpSPJrM3");
    }

    //user regist
    /*
    * @param username
    * @param password
    * @param emailAddress
     */
    public static AVUser userRegist(String username, String password, String emailAddress, final Context context){
        AVUser user = new AVUser();
        user.setUsername(username);
        user.setPassword(password);
        if(!emailAddress.equals("")) user.setEmail(emailAddress);
        return user;
    }

    //judge whether user is in state of login
    public static boolean isUserLogin(){
        AVUser currentUser = AVUser.getCurrentUser();
        if(currentUser != null){
            return true;
        }else{
            return false;
        }
    }

    //user logout
    public static void userLogout(){
        AVUser.logOut();
    }
}
