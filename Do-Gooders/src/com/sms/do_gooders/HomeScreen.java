package com.sms.do_gooders;



import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreen extends FragmentActivity implements ActionBar.TabListener,NavigationDrawerFragment.NavigationDrawerCallbacks 
{
	private ViewPager viewPager;
    private TabPagerAdapter mAdapter;
    private ActionBar actionBar;
    Button donateButton;
    private CharSequence mTitle;
    // Tab titles
    private String[] tabs = { "Pending", "Awarded" ,"Completed"};
    ImageView  menuImg,imageView;
    int tabPosition = -1;
    
    private NavigationDrawerFragment mNavigationDrawerFragment;
	private DrawerLayout mDrawerLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		TextView abTitle = (TextView) findViewById(titleId);
		abTitle.setTextColor(Color.WHITE);

		
		LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.custom_top_bar, null);


		 ActionBar actionBar = getActionBar();
		 actionBar.setDisplayOptions(actionBar.getDisplayOptions()
		            | ActionBar.DISPLAY_SHOW_CUSTOM);
		 actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation));
		    
		 donateButton  = new Button(getApplicationContext());
		 donateButton.setText("Donate");
		 donateButton.setBackgroundResource(R.drawable.custom_button);
		   
		    ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
		            ActionBar.LayoutParams.WRAP_CONTENT,
		            ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
		                    | Gravity.CENTER_VERTICAL);
		    layoutParams.rightMargin = 40;
		    layoutParams.bottomMargin = 5;
		    layoutParams.topMargin = 5;
		    donateButton.setLayoutParams(layoutParams);
		    actionBar.setCustomView(donateButton);
		    actionBar.setDisplayShowHomeEnabled(true);
		 
		    mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
					.findFragmentById(R.id.navigation_drawer);
		    
	 //Donate Button Code
		 donateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeScreen.this,DonationPage.class);
				startActivity(intent);
			}
		});
        
     // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        viewPager.beginFakeDrag();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        
     // Set up the drawer.
	mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
									(DrawerLayout) findViewById(R.id.drawer_layout));
	
	//set Custom fonts
	final Typeface mFont = Typeface.createFromAsset(getAssets(),
			"fonts/OpenSans-Regular.ttf"); 
	final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
	ServerConfig.setAppFont(mContainer, mFont,true);
	
	 this.getActionBar().setDisplayHomeAsUpEnabled(true);
     		 
        
    }
    
    public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle ="Sree";
			break;
		case 2:
			mTitle = "Sreenath";
			break;
		
		}
	}
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);
            return rootView;
        }
    }

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		tabPosition = tab.getPosition();
		viewPager.setCurrentItem(tab.getPosition());

	}


	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// TODO Auto-generated method stub
		// update the main content by replacing fragments
				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(R.id.container,
								NewPlaceholderFragment.newInstance(position + 1)).commit();
				
				switch (position) {
				case 0:
					//Go to My Notifications Page
					Intent i = new Intent(HomeScreen.this,NotificationsScreen.class);
					i.putExtra("fromNotifications", true);
					startActivity(i);
					break;
				case 1:
					//Go to  Profile Page
					Intent intent;
					intent = new Intent(HomeScreen.this,ProfileScreen.class);
					intent.putExtra("isInitialSetup", false);
					startActivity(intent);
					break;
				default:
					break;
				}
	}
	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class NewPlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static NewPlaceholderFragment newInstance(int sectionNumber) {
			NewPlaceholderFragment fragment = new NewPlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public NewPlaceholderFragment() {
		}


		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((HomeScreen) activity).onSectionAttached(getArguments()
					.getInt(ARG_SECTION_NUMBER));
		}
	}
}
