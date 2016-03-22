package com.example.rogerzzzz.cityrecall.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.UUID;

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

    //save pic array
    public static ArrayList<String> savePic(ArrayList<Uri> uriArrayList, Activity context) throws FileNotFoundException, AVException {
        ArrayList<String> arrayList = new ArrayList<String>();
        int arrLen = uriArrayList.size();

        for(int i = 0; i < arrLen; i++){
            String picPath = ImageUtils.getImageAbsolutePath(context, uriArrayList.get(i));
            String randomStr = UUID.randomUUID().toString();
            arrayList.add(randomStr);
            AVFile file;
            file = AVFile.withAbsoluteLocalPath(randomStr, picPath);
            file.save();
        }
        return arrayList;
    }
}
