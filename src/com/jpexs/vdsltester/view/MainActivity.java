package com.jpexs.vdsltester.view;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jpexs.vdsltester.R;
import com.jpexs.vdsltester.controller.Arbiter;
import com.jpexs.vdsltester.controller.MyListener;
import com.jpexs.vdsltester.model.Main;
import com.jpexs.vdsltester.model.routers.ComtrendRouter;
import com.jpexs.vdsltester.model.routers.HuaweiRouter;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener,
		SharedPreferences.OnSharedPreferenceChangeListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		if (Main.view != null) {
			View v=mViewPager.findViewWithTag(tab.getPosition());
			
			Main.view.setView(v);
			Main.view.setCard(cardNames[tab.getPosition()]);
			Main.updateValues();
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		onTabSelected(tab,fragmentTransaction);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 8;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1);
			case 1:
				return getString(R.string.title_section2);
			case 2:
				return getString(R.string.title_section3);
			case 3:
				return getString(R.string.title_section4);
			case 4:
				return getString(R.string.title_section5);
			case 5:
				return getString(R.string.title_section6);
			case 6:
				return getString(R.string.title_section7);
			case 7:
				return getString(R.string.title_section8);
			}
			return null;
		}
	}

	private static String cardNames[] = new String[] { "INFO", "1DAY", "15MIN",
			"HLOG", "GRAPHBIT", "GRAPHSNR", "GRAPHQLN", "GRAPHHLOG" };
	private boolean settingsChanged = false;
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onResume() {
		super.onResume();
		hideError();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		// register preference change listener
		prefs.registerOnSharedPreferenceChangeListener(this);

		if ((Main.router == null) || (settingsChanged)) {
			if (Main.router != null) {
				Main.router.disconnect();
			}
			String modemType=prefs.getString("modem", "comtrend");
			if(modemType.equals("comtrend")){
				Main.router = new ComtrendRouter();
			}
			if(modemType.equals("huawei")){
				Main.router = new HuaweiRouter();
			}
			Main.router.setFakeFile(Main.fakeFile);
			Main.connectionPassword = prefs.getString("password", "admin");
			Main.connectionUserName = prefs.getString("login", "admin");
			Main.router.setConnectionUserName(Main.connectionUserName);
			Main.router.setConnectionPassword(Main.connectionPassword);
			Main.router.setAddressAndPort(prefs.getString("ip", "10.0.0.138"),23);
			settingsChanged = false;
		}

		hideError();
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

	}

	public void hideError() {
		final TextView tw = (TextView) findViewById(R.id.errorTextView);
		tw.post(new Runnable() {
			public void run() {
				tw.setText("");
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Main.stop();
		if (Main.router != null) {
			Main.router.disconnect();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						selectInSpinnerIfPresent(position,true);
					}
					
					
					private void selectInSpinnerIfPresent(int position, boolean animate) {
					    try {
					        View actionBarView = null;
					        if (actionBarView == null) {
					            int id = getResources().getIdentifier("action_bar", "id", "android");
					            actionBarView = findViewById(id);
					        }
					 
					        Class<?> actionBarViewClass = actionBarView.getClass();
					        Field mTabScrollViewField = actionBarViewClass.getDeclaredField("mTabScrollView");
					        mTabScrollViewField.setAccessible(true);
					 
					        Object mTabScrollView = mTabScrollViewField.get(actionBarView);
					        if (mTabScrollView == null) {
					            return;
					        }
					 
					        Field mTabSpinnerField = mTabScrollView.getClass().getDeclaredField("mTabSpinner");
					        mTabSpinnerField.setAccessible(true);
					 
					        Object mTabSpinner = mTabSpinnerField.get(mTabScrollView);
					        if (mTabSpinner == null) {
					            return;
					        }
					 
					        Method setSelectionMethod = mTabSpinner.getClass().getSuperclass().getDeclaredMethod("setSelection", Integer.TYPE, Boolean.TYPE);
					        setSelectionMethod.invoke(mTabSpinner, position, animate);
					 
					        Method requestLayoutMethod = mTabSpinner.getClass().getSuperclass().getDeclaredMethod("requestLayout");
					        requestLayoutMethod.invoke(mTabSpinner);
					    } catch (IllegalArgumentException e) {
					        e.printStackTrace();
					    } catch (IllegalAccessException e) {
					        e.printStackTrace();
					    } catch (NoSuchFieldException e) {
					        e.printStackTrace();
					    } catch (NoSuchMethodException e) {
					        e.printStackTrace();
					    } catch (InvocationTargetException e) {
					        e.printStackTrace();
					    }
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		Main.main(new String[] {});
		Main.view.currentActivity = this;
		Main.view.setCard("INFO");
		Arbiter.listen("exception", new MyListener() {

			@Override
			public void eventHandler(String event, final Object data) {
				final TextView tw = (TextView) findViewById(R.id.errorTextView);
				tw.post(new Runnable() {
					public void run() {
						((Exception) data).printStackTrace();
						tw.setText(R.string.connectionError);
					}
				});

			}

		});
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		hideError();
		settingsChanged = true;
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent myIntent = new Intent(this, ConnectionSettingsActivity.class);
			startActivityForResult(myIntent, 0);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * @Override public boolean onNavigationItemSelected(int position, long id)
	 * { // When the given dropdown item is selected, show its contents in the
	 * // container view. DummySectionFragment fragment = new
	 * DummySectionFragment(); Bundle args = new Bundle();
	 * args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
	 * fragment.setArguments(args);
	 * getSupportFragmentManager().beginTransaction() .replace(R.id.container,
	 * fragment) .commit(); return true; }
	 */

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = null;
			int cid = getArguments().getInt(ARG_SECTION_NUMBER);
			switch (cid) {
			case 0:
				rootView = inflater.inflate(R.layout.info, container, false);
				break;
			case 1:
				rootView = inflater.inflate(R.layout.oneday, container, false);
				break;
			case 2:
				rootView = inflater.inflate(R.layout.fifteen_minutes,
						container, false);
				break;
			case 3: // hlog
				rootView = inflater.inflate(R.layout.hlog, container, false);
				break;
			case 4:
				rootView = inflater.inflate(R.layout.graph_bit, container,
						false);
				break;
			case 5:
				rootView = inflater.inflate(R.layout.graph_snr, container,
						false);
				break;
			case 6:
				rootView = inflater.inflate(R.layout.graph_qln, container,
						false);
				break;
			case 7:
				rootView = inflater.inflate(R.layout.graph_hlog, container,
						false);
				break;
			default:
				return null;
			}
			rootView.setTag(cid);
			String sc = cardNames[cid];

			return rootView;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

}
