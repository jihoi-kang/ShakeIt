package com.example.kjh.shakeit.app;

import android.app.Activity;

import com.example.kjh.shakeit.main.MainActivity;

import java.util.Stack;

/**
 * 로그인 후 앱의 액티비티 관리 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 5. 27. PM 8:41
 **/
public class AppManager {

    private static Stack<Activity> activityStack;
    private volatile static AppManager instance;

    private AppManager() {
    }

    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                    instance.activityStack = new Stack();
                }
            }

        }
        return instance;
    }

    /**------------------------------------------------------------------
     메서드 ==> 쌓여있는 액티비티 중에 MainActivity Get
     ------------------------------------------------------------------*/
    public synchronized MainActivity getMain() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i) && activityStack.get(i).getClass().getName().equals(MainActivity.class.getName())) {
                if (activityStack.get(i) instanceof MainActivity)
                    return (MainActivity) activityStack.get(i);
            }
        }
        return null;
    }

    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    /**------------------------------------------------------------------
     메서드 ==> 액티비티 스택 추가
     ------------------------------------------------------------------*/
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**------------------------------------------------------------------
     메서드 ==> 현재 액티비티
     ------------------------------------------------------------------*/
    public Activity currentActivity() {
        try {
            Activity activity = activityStack.lastElement();
            return activity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**------------------------------------------------------------------
     메서드 ==> 액티비티 스택 삭제
     ------------------------------------------------------------------*/
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

}
