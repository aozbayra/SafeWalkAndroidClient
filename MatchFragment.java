package edu.purdue.aozbayra;

import java.io.*;
import java.net.*;

import android.app.Fragment;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This fragment is the "page" where the application display the log from the
 * server and wait for a match.
 *
 * @author Alihan Ozbayrak
 * @author William Finger
 */
public class MatchFragment extends Fragment implements OnClickListener {

	private static final String DEBUG_TAG = "DEBUG";

	/**
	 * Activity which have to receive callbacks.
	 */
	private StartOverCallbackListener activity;

	/**
	 * AsyncTask sending the request to the server.
	 */
	private Client client;

	/**
	 * Coordinate of the server.
	 */
	private String host;
	private int port;

	/**
	 * Command the user should send.
	 */
	private String command;

	// TODO: your own class fields here
	
	
	private TextView clientInfo;	
	private TextView partnerInfo;
	private TextView partnerFromInfo;
	private TextView partnerToInfo;
	private TextView serverInfo;
	private TextView resultInfo;

	private String name;
	private String from;
	private String to;
	private int type;
	private Socket clientSocket;
	private InetAddress serverAddr;
	private String response;
	private String responseArray[] = new String[3];
	

	// Class methods
	/**
	 * Creates a MatchFragment
	 * 
	 * @param activity
	 *            activity to notify once the user click on the start over
	 *            Button.
	 * @param host
	 *            address or IP address of the server.
	 * @param port
	 *            port number.
	 * 
	 * @param command
	 *            command you have to send to the server.
	 * 
	 * @return the fragment initialized.
	 */
	// TODO: you can add more parameters, follow the way we did it.
	// ** DO NOT CREATE A CONSTRUCTOR FOR MatchFragment **
	public static MatchFragment newInstance(StartOverCallbackListener activity,
			String host, int port, String command, String name, String from, String to, int type) {
		MatchFragment f = new MatchFragment();
		

		f.activity = activity;
		f.host = host;
		f.port = port;
		f.command = command;
		f.name = name;
		f.from = from;
		f.to = to;
		f.type = type;
		

		return f;
	}

	/**
	 * Called when the fragment will be displayed.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		View view = inflater.inflate(R.layout.match_fragment_layout, container,
				false);

		/**
		 * Register this fragment to be the OnClickListener for the startover
		 * button.
		 */
		view.findViewById(R.id.bu_start_over).setOnClickListener(this);

		// TODO: import your Views from the layout here. See example in
		// ServerFragment.
		this.serverInfo = (TextView) view.findViewById(R.id.tv_serverinfo);
		this.clientInfo = (TextView) view.findViewById(R.id.tv_client);
		this.resultInfo = (TextView) view.findViewById(R.id.tv_result);
		this.partnerInfo = (TextView) view.findViewById(R.id.tv_partnerInfo);
		this.partnerFromInfo = (TextView) view.findViewById(R.id.tv_partnerFromInfo);
		this.partnerToInfo = (TextView) view.findViewById(R.id.tv_partnerToInfo);
		
		/**
		 * Launch the AsyncTask
		 */
		this.client = new Client();
		this.client.execute("");

		return view;
	}

	/**
	 * Callback function for the OnClickListener interface.
	 */
	@Override
	public void onClick(View v) {
		/**
		 * Close the AsyncTask if still running.
		 */
		this.client.close();

		/**
		 * Notify the Activity.
		 */
		this.activity.onStartOver();
	}

	class Client extends AsyncTask<String, String, String> implements Closeable {

		/**
		 * NOTE: you can access MatchFragment field from this class:
		 * 
		 * Example: The statement in doInBackground will print the message in
		 * the Eclipse LogCat view.
		 */

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		protected String doInBackground(String... params) {

			/**
			 * TODO: Your Client code here.
			 */
			try {
				serverAddr = InetAddress.getByName(host);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			try {
				clientSocket = new Socket(serverAddr, port);
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				command = name + "," + from + "," + to + "," + type;
				out.println(command);
				response = in.readLine();
				responseArray = extractTokens(response);			
			}
			catch (Exception e) {
				publishProgress(String
					.format("The Server at the address %s uses the port %d is unavailable",
							host, port));
				
			}
			Log.d(DEBUG_TAG, String
					.format("The Server at the address %s uses the port %d",
							host, port));

			Log.d(DEBUG_TAG, String.format(
					"The Client will send the command: %s", command));
			
			return "";
		}
		
		private String[] extractTokens(String input) {
			  char[] charArray = input.toCharArray();
			  int[] commaIndex = new int[3];
			  //Index of space after "RESPONSE:"
			  int spaceIndex = 10;
			  int c = 0;
			  String[] error = { "Error" };

			  for (int i = 0; i < charArray.length; i++) {
			   if (charArray[i] == ',') {
			    commaIndex[c] = i;
			    c++;
			   }
			  }

			  if (c != 3)
			   return error;

			  String name = input.substring(spaceIndex, commaIndex[0]);
			  System.out.println(name);
			  String from = input.substring(commaIndex[0] + 1, commaIndex[1]);
			  String to = input.substring(commaIndex[1] + 1, commaIndex[2]);
			  String[] tokens = { name, from, to };

			  return tokens;
			 }
		public void close() {
			name = "";
			type = 1;
			from = "";
			to = "";
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */

		// TODO: use the following method to update the UI.
		// ** DO NOT TRY TO CALL UI METHODS FROM doInBackground!!!!!!!!!! **

		/**
		 * Method executed just before the task.
		 */
		@Override
		protected void onPreExecute() {
			clientInfo.setText(name + " with type " + type + " sent a request to move from " + from + " to " + to);
			serverInfo.setText("Connecting to the server...");
			resultInfo.setText("Matching is in progress...");
		}

		/**
		 * Method executed once the task is completed.
		 */
		@Override
		protected void onPostExecute(String result) {
			partnerInfo.setText(responseArray[0]);
			partnerFromInfo.setText(responseArray[1]);
			partnerToInfo.setText(responseArray[2]);
		}

		/**
		 * Method executed when progressUpdate is called in the doInBackground
		 * function.
		 */
		@Override
		protected void onProgressUpdate(String... result) {
			if (clientSocket.isConnected())		
				serverInfo.setText("Connection to the server is succesfull");
			else 
				serverInfo.setText("Connection to the server failed");
		}
	}
}
