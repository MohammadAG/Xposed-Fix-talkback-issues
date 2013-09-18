package com.mohammadag.fixtalkbackissues;

import android.content.Context;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class FixTalkbackIssues implements IXposedHookLoadPackage {

	// Sadly, most of Samsung apps consider accessilibity to only equal Talkback,
	// so instead of checking if talkback is actually enabled, they simply check
	// if accessibility is enabled. While we could do a huge universal hook that
	// always returns false for that check, that might break other apps that 
	// actually use accessibility.
	
	// This module should work on a lot of devices, so we use a lot of try...catch
	// statements.
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("com.sec.android.app.videoplayer")) {
			// The video player on the Galaxy S4 refuses to hide the UI of the player
			// while accessibility is enabled, so tapping on the screen doesn't do 
			// anything. A fix is to rotate the phone, or hook the method that checks
			// if talkback is on.
			String vUtilsClassName = "com.sec.android.app.videoplayer.common.VUtils";
			try {
				Class<?> VUtils = XposedHelpers.findClass(vUtilsClassName, lpparam.classLoader);
				
				XposedHelpers.findAndHookMethod(VUtils, "isTalkBackOn", Context.class,
						XC_MethodReplacement.returnConstant(false));
			} catch (ClassNotFoundError e) {
				logNotHookingClass(vUtilsClassName);
			} catch (NoSuchMethodError e) {
				logNotHookingMethod("isTalkBackOn");
			}
		}
		
		if (lpparam.packageName.equals("com.android.settings")) {
			// Scanning for Wi-Fi networks says "Scan finished".
			String wiFiSettingsClassName = "com.android.settings.wifi.WifiSettings";
			try {
				Class<?> WiFiSettings = XposedHelpers.findClass(wiFiSettingsClassName, lpparam.classLoader);
				XposedHelpers.findAndHookMethod(WiFiSettings, "speakTTS", XC_MethodReplacement.DO_NOTHING);
			} catch (ClassNotFoundError e) { 
				logNotHookingClass(wiFiSettingsClassName);
			} catch (NoSuchMethodError e) {
				logNotHookingMethod("speakTTS");
			}
		}
		
		if (lpparam.packageName.equals("com.android.launcher2")) {
			// Opening/closing a folder in launcher says "Folder opened/closed"
			String launcherClassName = "com.android.launcher2.Launcher";
			try {
				Class<?> Launcher = XposedHelpers.findClass(launcherClassName, lpparam.classLoader);
				XposedHelpers.findAndHookMethod(Launcher, "isTalkbackSuspend",
						XC_MethodReplacement.returnConstant(true));
			} catch (ClassNotFoundError e) {
				logNotHookingClass(launcherClassName);
			} catch (NoSuchMethodError e) {
				logNotHookingMethod("isTalkbackSuspend");
			}
		}
	}
	
	private void logNotHookingClass(String className) {
		XposedBridge.log("Not hooking class: " + className);
	}
	
	private void logNotHookingMethod(String methodName) {
		XposedBridge.log("Not hooking method: " + methodName);
	}

}
