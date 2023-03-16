package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.guoji.mobile.cocobee.R;


public class SplashAct extends BaseAct {

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = getSharedPreferences("bluetooth", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
		editor.putBoolean("status", false); 
		editor.commit();



//		DbController dbController = DbController.getInstance(SplashAct.this);
//		dbController.openDatabase();
//		final List<User> users = dbController.findAllUserInfo();
//		dbController.closeDatabase();
//		if(users != null && users.size() > 0 ){ //把当前用户设置到域中
//			user = users.get(0);
//			app.setUser(user);
//		}

		final Intent intent;
		if(user != null){
			intent = new Intent(SplashAct.this,MainActivity1.class);
		}else {
			intent = new Intent(SplashAct.this,LoginActivity.class);
		}
//		Timer timer = new Timer();
//		TimerTask task = new TimerTask() {
//			@Override
//			public void run() {
//				startActivity(intent); //执行
//				finish();
//			}
//		};
//		timer.schedule(task, 1000 * 3); //10秒后
		startActivity(intent);
		finish();

	}



}
