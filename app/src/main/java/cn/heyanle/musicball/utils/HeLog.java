package cn.heyanle.musicball.utils;

import android.util.Log;

import cn.heyanle.musicball.C;

public class HeLog {

    /**
     * 包装原始Log的v,d,i,w,e方法
     * 重写两个
     * 第一个直接传入msg
     * 第二个传入value在传入msg 输出为 value -> msg
     *
     * 最后一个参数是一个对象，以这个对象的类完整名（包括包名）做TAG
     */

    public static void v(String value,Object classInit){
        if (C.IS_DEBUG) Log.v(classInit.getClass().getName(),value);
    }

    public static void v(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.v(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void d(String value,Object classInit){
        if (C.IS_DEBUG) Log.d(classInit.getClass().getName(),value);
    }

    public static void d(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.d(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void i(String value,Object classInit){
        if (C.IS_DEBUG) Log.i(classInit.getClass().getName(),value);
    }

    public static void i(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.i(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void w(String value,Object classInit){
        if (C.IS_DEBUG) Log.w(classInit.getClass().getName(),value);
    }

    public static void w(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.w(classInit.getClass().getName(),value + " -> " + msg);
    }

    public static void e(String value,Object classInit){
        if (C.IS_DEBUG) Log.e(classInit.getClass().getName(),value);
    }

    public static void e(String value,String msg,Object classInit){
        if (C.IS_DEBUG) Log.e(classInit.getClass().getName(),value + " -> " + msg);
    }

}
