package cn.heyanle.musicball;

import android.content.Context;
import android.content.SharedPreferences;

import cn.heyanle.musicball.utils.HeLog;

import static android.content.Context.MODE_PRIVATE;

public class DataMonitor {

    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor;

    public DataMonitor(Context context){
        preferences = context.getSharedPreferences("data",MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getString(String name,String def){
        HeLog.i("get > " + name,preferences.getString(name,def),this);
        return preferences.getString(name,def);
    }

    public int getInt(String name,int def){
        HeLog.i("get > " + name,preferences.getInt(name,def) + "",this);
        return preferences.getInt(name,def);
    }

    public float getFloat(String name,float def){
        HeLog.i("get > " + name,preferences.getFloat(name,def) + "",this);
        return preferences.getFloat(name,def);
    }

    public void setString(String name,String val){
        HeLog.i("set > " + name,val + "",this);
        editor.putString(name,val);
    }

    public void setInt(String name,int val){
        HeLog.i("set > " + name,val + "",this);
        editor.putInt(name,val);
    }

    public void setFloat(String name,float val){
        HeLog.i("set > " + name,val + "",this);
        editor.putFloat(name,val);
    }

    public void apply(){
        editor.apply();
    }

}
