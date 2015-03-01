package com.jpexs.vdsltester.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;

import com.jpexs.vdsltester.controller.Arbiter;
import com.jpexs.vdsltester.controller.Controller;
import com.jpexs.vdsltester.controller.MyListener;
import com.jpexs.vdsltester.model.routers.ComtrendRouter;
import com.jpexs.vdsltester.model.routers.HuaweiRouter;
import com.jpexs.vdsltester.view.ConnectionSettingsActivity;
import com.jpexs.vdsltester.view.View;

/**
 * 
 * @author JPEXS
 */
public class Main {
	private static boolean debugMode = false;
	public static View view;
	public static Controller controller;
	public static Router router;
	public static String connectionUserName = "admin";
	public static String connectionPassword = "admin";
	public static List<Router> routers = new ArrayList<Router>();
	public static String defaultIP = "10.0.0.138";
	public static int defaultPort = 23;
	public static int socketTimeout = 5000;
	public static int delay = 5000;
	public static String fakeFile = null;
	public static String version = "beta 6.1";
	public static boolean loadOnceMode = false;

	static {
		routers.add(new ComtrendRouter());
		routers.add(new HuaweiRouter());
	}

	public static void loadConfig() {
		File settingsFile = new File("vdsltester.cfg");
		if (settingsFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(
						settingsFile));
				String s = "";
				while ((s = br.readLine()) != null) {
					if (s.startsWith("loadFake=")) {
						fakeFile = s.substring(s.indexOf("=") + 1);
					}
					if (s.startsWith("language=")) {
						View.langId = s.substring(s.indexOf("=") + 1);
					}
					if (s.startsWith("timeout=")) {
						try {
							socketTimeout = Integer.parseInt(s.substring(s
									.indexOf("=") + 1));
						} catch (NumberFormatException nex) {

						}
					}
					if (s.startsWith("defaultIP=")) {
						defaultIP = s.substring(s.indexOf("=") + 1);
					}
					if (s.startsWith("defaultUserName=")) {
						connectionUserName = s.substring(s.indexOf("=") + 1);
					}
					if (s.startsWith("defaultPassword=")) {
						connectionPassword = s.substring(s.indexOf("=") + 1);
					}
					if (s.startsWith("defaultPort=")) {
						try {
							defaultPort = Integer.parseInt(s.substring(s
									.indexOf("=") + 1));
						} catch (NumberFormatException nex) {
							defaultPort = 23;
						}
					}
					if (s.startsWith("debugMode=1")) {
						setDebugMode(true);
					}
					if (s.startsWith("loadOnce=1")) {
						loadOnceMode = true;
					}
				}
				br.close();
			} catch (IOException ex) {

			}
		}
	}

	public static void setDebugMode(boolean debugMode) {
		Main.debugMode = debugMode;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	/*
	 * public static void eveMain(String[] args) { if(eve.sys.Device.isMobile())
	 * { eve.sys.Device.setScreenRotation(eve.sys.Device.ROTATION_90); } }
	 */

	public static void main(String[] args) {
		/*
		 * Application.startApplication(args); loadConfig(); try{
		 * Window.defaultWindowIcon = Device.toDeviceIcon("ikona.gif");//new
		 * Picture( }catch(Exception ex){}
		 */
		if (isDebugMode()) {
			Arbiter.listen("exception", new MyListener() {
				@Override
				public void eventHandler(String event, Object data) {
					ProgramLog.printException((Exception) data);
				}
			});
		}

		controller = new Controller();
		view = new View();

	}

	public static final Object lock = new Object();;

	public static void exit() {
		if (router != null) {
			Arbiter.inform("terminating");
			synchronized (lock) {
				router.disconnect();
			}
		}
		terminated();
	}

	public static void terminating() {
		if (router != null) {
			router.setFinishing();
		}
	}

	public static void terminated() {
		Arbiter.inform("terminated");
		// Application.exit(0);
	}

	private static Thread thr;
	
	public static void stop(){
		if(thr!=null){
			thr.interrupt();
			thr = null;
		}
	}

	public static void updateValues() {
		if (thr == null) {
			thr = new Thread() {
				@Override
				public void run() {
					try {						
						int cnt = 0;
						String lastCardName="none";
						int waitCnt=0;
						do {
							String cardName = view.getSelectedCard();
							if (cardName.equals(lastCardName) && waitCnt<10) {
								Thread.sleep(500);
								waitCnt++;
								continue;
							}
							waitCnt = 0;
							HashSet<String> needs = view.getNeededFields(cardName);																					
							synchronized (lock) {
								try {
									router.login();
									Arbiter.inform("doMeasureStart");
									RouterMeasurement rm = router
											.doMeasure(needs);
									Arbiter.inform("doMeasureFinish");
									Arbiter.inform("finalupdateStart");
									view.updateFields(rm, cardName);
									Arbiter.inform("finalupdateFinish");
								} catch (FinishException fex) {
									Arbiter.inform("measureTerminated");
								} catch (IOException iex) {
									router.forceDisconnect();
									Arbiter.inform("exception", iex);
								} catch (Exception ex) {
									router.disconnect();
									Arbiter.inform("exception", ex);
								}
							}
							if (Main.loadOnceMode) {
								thr = null;
								break;
							}							
						} while (!this.isInterrupted());

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			if (thr != null)
				thr.start();
		}
	}

	public static double carrierToFrequency(int carrier) {
		return ((double) carrier) / (232.0 / 1000.0);
	}
}
