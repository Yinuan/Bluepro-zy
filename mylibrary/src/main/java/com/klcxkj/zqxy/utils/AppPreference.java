package com.klcxkj.zqxy.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.klcxkj.zqxy.databean.IDCardData;
import com.klcxkj.zqxy.databean.MsgQueryList;
import com.klcxkj.zqxy.databean.WashingModelInfo;

public class AppPreference {
    public static final String COUPON_CFG_XML = "klcxkj_config.xml";
    
    private static final String SEED = "klcxkj";
    
    private static final String USER_ID = "user_id";
    private static final String IS_FIRST_TIME_OPEN = "IS_FIRST_TIME_OPEN";
    
    private SharedPreferences mCfgPreference;
    
    
    private static AppPreference mInstance;

    private AppPreference() {
    }

    public void init(Context ctx) {
        if (mCfgPreference == null) {
            mCfgPreference = ctx.getSharedPreferences(COUPON_CFG_XML, Context.MODE_PRIVATE);
        }
    }

    public static AppPreference getInstance() {
        if (mInstance == null) {
            mInstance = new AppPreference();
        }
        return mInstance;
    }
    
    public void clear() {
    	SharedPreferences.Editor editor = mCfgPreference.edit();
    	editor.clear();
    }

    public boolean save(String key, String value){
    	SharedPreferences.Editor editor = mCfgPreference.edit();
    	editor.putString(key, value);
    	return editor.commit();
    }

    public boolean save(String key, boolean value){
    	SharedPreferences.Editor editor = mCfgPreference.edit();
    	editor.putBoolean(key, value);
    	return editor.commit();
    }
    
    public String get(String key, String defaultValue){
    	return mCfgPreference.getString(key, defaultValue);
    }
    
    public boolean get(String key, boolean defaultValue){
    	return mCfgPreference.getBoolean(key, defaultValue);
    }
    
    public boolean isFirstTimeOpen(){
    	return mCfgPreference.getBoolean(IS_FIRST_TIME_OPEN, true);
    }
    
    public void setNotFirstTimeOpen(){
    	save(IS_FIRST_TIME_OPEN, false);
    }


    /**
     * 洗澡
     * @param info
     */
    public void saveWatchBind0(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("watchBindInfo0", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    /**
     * 洗澡
     * @return
     */
    public String getWatchBind0(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("watchBindInfo0", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }



    /**
     * 饮水
     * @param info
     */
    public void saveWatchBind1(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("watchBindInfo1", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    /**
     * 饮水
     * @return
     */
    public String getWatchBind1(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("watchBindInfo1", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }



    /**
     * 洗衣机
     * @param info
     */
    public void saveWatchBind2(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("watchBindInfo2", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    /**
     * 洗衣机
     * @return
     */
    public String getWatchBind2(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("watchBindInfo2", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     * 吹风机
     * @param info
     */
    public void saveWatchBind3(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("watchBindInfo3", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    /**
     * 吹风机
     * @return
     */
    public String getWatchBind3(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("watchBindInfo3", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     * 充电器
     * @param info
     */
    public void saveWatchBind4(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("watchBindInfo4", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    /**
     * 充电器
     * @return
     */
    public String getWatchBind4(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("watchBindInfo4", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     *
     * @param idCardData
     */
    public void saveMyNameSexIdCard(IDCardData idCardData){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            Gson gson = new Gson();
            try {

                localEditor.putString("saveMyNameSexIdCard", SimpleCrypto.encrypt(SEED, gson.toJson(idCardData)));
            } catch (Exception e) {
                e.printStackTrace();
                localEditor.putString("saveMyNameSexIdCard", gson.toJson(idCardData));
            }
            localEditor.commit();
        }
    }

    public IDCardData getMyIDCardData(){
        if (mCfgPreference != null) {
            String encyptPhone = mCfgPreference.getString("saveMyNameSexIdCard", "");
            if(!StringUtils.isEmpty(encyptPhone))
                try {
                    String json = SimpleCrypto.decrypt(SEED, encyptPhone);
                    Gson gson = new Gson();
                    return gson.fromJson(json, IDCardData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     *
     * @param washingModelInfo
     */
    public void saveWashingModelInfo(WashingModelInfo washingModelInfo){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                Gson gson = new Gson();
                localEditor.putString("savewashingModelInfo", SimpleCrypto.encrypt(SEED, gson.toJson(washingModelInfo)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    public WashingModelInfo getWashingModelInfo(){
        if (mCfgPreference != null) {
            String encyptPhone = mCfgPreference.getString("savewashingModelInfo", "");
            if(!StringUtils.isEmpty(encyptPhone))
                try {
                    String json = SimpleCrypto.decrypt(SEED, encyptPhone);
                    Gson gson = new Gson();
                    return gson.fromJson(json, WashingModelInfo.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     *  缓存消息类
     * @param
     */
    public void saveMessage(MsgQueryList result){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                Gson gson = new Gson();
                localEditor.putString("querySpread", SimpleCrypto.encrypt(SEED, gson.toJson(result)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    /**
     * 取出消息类
     * @return
     */
    public MsgQueryList getMessage(){
        if (mCfgPreference != null) {
            String encyptPhone = mCfgPreference.getString("querySpread", "");
            Gson gson = new Gson();
            if(!StringUtils.isEmpty(encyptPhone))
                try {
                    String json = SimpleCrypto.decrypt(SEED, encyptPhone);

                    return gson.fromJson(json, MsgQueryList.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return gson.fromJson(encyptPhone, MsgQueryList.class);
                }
        }
        return null;
    }
    /**
     * 获取洗衣机开始的时间
     * @param
     * @return
     */
    public String getWashingTime(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("time_old", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                    return passWord;
                }
        }
        return null;
    }
    /**
     * 保存洗衣开始的时间
     * @param info
     */
    public void saveWashingTime(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("time_old", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
                localEditor.putString("time_old", info);
            }
            localEditor.commit();
        }
    }

    /**
     * 清除设备绑定信息
     */

    public void deleteWashingModel(){
        if (mCfgPreference!=null){
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            localEditor.remove("savewashingModelInfo");
            localEditor.commit();
        }
    }

    /**
     * 保存退卡的ID
     * @param info
     */
    public void saveCardReturn(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("card_return", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
            }
            localEditor.commit();
        }
    }

    /**
     * 洗澡
     * @return
     */
    public String getCardRenturnInfo(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("card_return", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }



    /**
     * 获取服务器地址
     * @param
     * @return
     */
    public String getZyIp(){
        if (mCfgPreference != null) {
            String passWord = mCfgPreference.getString("zy_ip", "");
            if(!StringUtils.isEmpty(passWord))
                try {
                    String json = SimpleCrypto.decrypt(SEED, passWord);
                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                    return passWord;
                }
        }
        return null;
    }
    /**
     * 保存服务器地址
     * @param info
     */
    public void saveZyIp(String info){
        if (mCfgPreference != null) {
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            try {
                localEditor.putString("zy_ip", SimpleCrypto.encrypt(SEED, info));
            } catch (Exception e) {
                e.printStackTrace();
                localEditor.putString("zy_ip", info);
            }
            localEditor.commit();
        }
    }

    /**
     * 清除设备绑定信息
     */

    public void deleteDeciveBindInfo(){
        if (mCfgPreference!=null){
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            localEditor.remove("saveMyNameSexIdCard");
            localEditor.commit();
        }
    }






    /**
     * 清除首页消息缓存
     */

    public void deleteQuerySpread(){
        if (mCfgPreference!=null){
            SharedPreferences.Editor localEditor = mCfgPreference.edit();
            localEditor.remove("querySpread");
            localEditor.commit();
        }
    }

}
