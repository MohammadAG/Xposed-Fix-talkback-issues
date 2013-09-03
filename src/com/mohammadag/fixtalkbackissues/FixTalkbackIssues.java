package com.mohammadag.fixtalkbackissues;

import android.content.Context;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class FixTalkbackIssues implements IXposedHookLoadPackage {

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("com.sec.android.app.videoplayer")) {
		
			// The video player UI refuses to hide because of this.
			XposedHelpers.findAndHookMethod("com.sec.android.app.videoplayer.common.VUtils",
					lpparam.classLoader, "isTalkBackOn", Context.class, new XC_MethodReplacement() {
						
						@Override
						protected Object replaceHookedMethod(MethodHookParam param)
								throws Throwable {
							return false;
						}
					});
		}
		
		if (lpparam.packageName.equals("com.android.settings")) {
			// Scanning for Wi-Fi networks says "Scan finished".
			XposedHelpers.findAndHookMethod("com.android.settings.wifi.WifiSettings", lpparam.classLoader,
					"speakTTS", XC_MethodReplacement.DO_NOTHING);
		}
		
	}

}
