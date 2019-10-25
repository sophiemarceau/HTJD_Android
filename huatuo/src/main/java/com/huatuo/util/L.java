package com.huatuo.util;

import android.util.Log;

/**
 * Logͳһ������
 * 
 * @author way
 * 
 */
public class L
{
    public static boolean isDebug = true;// �Ƿ���Ҫ��ӡbug��������application��onCreate���������ʼ��
    
    private static final String TAG = "way";
    
    public static final String SEPARATOR = ",";
    
    // �����ĸ���Ĭ��tag�ĺ���
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }
    
    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);
    }
    
    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }
    
    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }
    
    // �����Ǵ����Զ���tag�ĺ���
    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }
    
    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }
    
    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }
    
    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }
    
    /**
     * �����־����������Ϣ
     */
    public static String getLogInfo(StackTraceElement stackTraceElement)
    {
        StringBuilder logInfoStringBuilder = new StringBuilder();
        
        // ��ȡ�ļ���.��xxx.java
        String fileName = stackTraceElement.getFileName();
        // ��ȡ����.������+����
        String className = stackTraceElement.getClassName();
        // ��ȡ��������
        String methodName = stackTraceElement.getMethodName();
        // ��ȡ�����������
        int lineNumber = stackTraceElement.getLineNumber();
        
        logInfoStringBuilder.append("[ ");
        
        logInfoStringBuilder.append("fileName=" + fileName).append(SEPARATOR);
        logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
        logInfoStringBuilder.append("methodName=" + methodName)
                .append(SEPARATOR);
        logInfoStringBuilder.append("lineNumber=" + lineNumber);
        logInfoStringBuilder.append(" ] ");
        return logInfoStringBuilder.toString();
    }
}
