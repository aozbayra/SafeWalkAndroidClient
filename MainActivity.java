package edu.purdue.aozbayra;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SubmitCallbackListener,
		StartOverCallbackListener {

	/**
	 * The ClientFragment used by the activity.
	 */
	private ClientFragment clientFragment;

	/**
	 * The ServerFragment used by the activity.
	 */
	private ServerFragment serverFragment;

	/**
	 * UI component of the ActionBar used for navigation.
	 */
	private Button left;
	private Button right;
	private TextView title;

	/**
	 * Called once the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		this.clientFragment = ClientFragment.newInstance(this);
		this.serverFragment = ServerFragment.newInstance();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fl_main, this.clientFragment);
		ft.commit();
	}

	/**
	 * Creates the ActionBar: - Inflates the layout - Extracts the components
	 */
	@SuppressLint("InflateParams")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.action_bar, null);

		// Set up the ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);

		// Extract the UI component.
		this.title = (TextView) actionBarLayout.findViewById(R.id.tv_title);
		this.left = (Button) actionBarLayout.findViewById(R.id.bu_left);
		this.right = (Button) actionBarLayout.findViewById(R.id.bu_right);
		this.right.setVisibility(View.INVISIBLE);

		return true;
	}

	/**
	 * Callback function called when the user click on the right button of the
	 * ActionBar.
	 * 
	 * @param v
	 */
	public void onRightClick(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(this.getResources().getString(R.string.client));
		this.left.setVisibility(View.VISIBLE);
		this.right.setVisibility(View.INVISIBLE);
		ft.replace(R.id.fl_main, this.clientFragment);
		ft.commit();
	}

	/**
	 * Callback function called when the user click on the left button of the
	 * ActionBar.
	 * 
	 * @param v
	 */
	public void onLeftClick(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(this.getResources().getString(R.string.server));
		this.left.setVisibility(View.INVISIBLE);
		this.right.setVisibility(View.VISIBLE);
		ft.replace(R.id.fl_main, this.serverFragment);
		ft.commit();

	}

	/**
	 * Callback function called when the user click on the submit button.
	 */
	@Override
	public void onSubmit() {
		// TODO: Get client info via client fragment
		// Client info
		String name = this.clientFragment.getName();
		int type = this.clientFragment.getType();
		String from = this.clientFragment.getFrom();
		String to = this.clientFragment.getTo();

		// Server info
		String host = this.serverFragment.getHost(getResources().getString(
				R.string.default_host));
		int port = this.serverFragment.getPort(Integer.parseInt(getResources()
				.getString(R.string.default_port)));
		
		// TODO: sanity check the results of the previous two dialogs
		String serverCheck = serverIsValid(host, port);
		String clientCheck = clientIsValid(name, type, from, to);
		if (!clientCheck.equals("")) 
			buildAlert(clientCheck);
		else if (!serverCheck.equals(""))
			buildAlert(serverCheck);
		else {
			// TODO: Need to get command from client fragment
			String command = this.getResources()
					.getString(R.string.default_command);
	
			FragmentTransaction ft = getFragmentManager().beginTransaction();
	
			this.title.setText(getResources().getString(R.string.match));
			this.left.setVisibility(View.INVISIBLE);
			this.right.setVisibility(View.INVISIBLE);
	
			// TODO: You may want additional parameters here if you tailor
			// the match fragment
			MatchFragment frag = MatchFragment.newInstance(this, host, port,
					command, name, from, to, type);
	
			ft.replace(R.id.fl_main, frag);
			ft.commit();
		}
		
		
	}

	/**
	 * Callback function call from MatchFragment when the user want to create a
	 * new request.
	 */
	@Override
	public void onStartOver() {
		onRightClick(null);
	}
	
	public String serverIsValid(String host, int port) {
		char[] hostArray = host.toCharArray();
		if (host.equals(""))
			return "Host";
		for (char i : hostArray) {
			if (i == 32)
				return "Host";
		}
		if (port < 1 || port > 65535)
			return "Port";
		return "";
	}
	
	public String clientIsValid(String name, int type, String from, String to) {
		if (name.equals("") || name.contains(","))
			return "Name";
		if (type < 0 || type > 2)
			return "Type";
		ArrayList<String> locations = new ArrayList<String>(Arrays.asList("CL50", "EE", "LWSN", "PMU", "PUSH"));
		if (to.equals(from))
			return "To - From Combination";
		if (!locations.contains(from))
			return "From";
		locations.add("ANY");
		if (!locations.contains(to))
			return "To";
		if (to.equals("ANY")) {
			if (type != 2)
				return "To - Type Combination";
		}
		return "";
	}
	
	public void buildAlert(String invalidInfo) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Invalid Information");
		alertDialogBuilder.setMessage(String.format("Invalid %s Information", invalidInfo));
		alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	

}
