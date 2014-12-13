package edu.purdue.aozbayra;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * This fragment is the "page" where the user inputs information about the
 * request, he/she wishes to send.
 *
 * @author Alihan Ozbayrak
 */
public class ClientFragment extends Fragment implements OnClickListener {

	/**
	 * Activity which have to receive callbacks.
	 */
	private SubmitCallbackListener activity;
	
	private EditText name;
	private RadioGroup typeContainer;
	private Spinner from;
	private Spinner to;

	/**
	 * Creates a ProfileFragment
	 * 
	 * @param activity
	 *            activity to notify once the user click on the submit Button.
	 * 
	 *            ** DO NOT CREATE A CONSTRUCTOR FOR MatchFragment **
	 * 
	 * @return the fragment initialized.
	 */
	// ** DO NOT CREATE A CONSTRUCTOR FOR ProfileFragment **
	public static ClientFragment newInstance(SubmitCallbackListener activity) {
		ClientFragment f = new ClientFragment();

		f.activity = activity;
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

		View view = inflater.inflate(R.layout.client_fragment_layout,
				container, false);

		/**
		 * Register this fragment to be the OnClickListener for the submit
		 * Button.
		 */
		view.findViewById(R.id.bu_submit).setOnClickListener(this);

		// TODO: import your Views from the layout here. See example in
		// ServerFragment.
		
		this.name = (EditText) view.findViewById(R.id.et_name);
		this.typeContainer = (RadioGroup) view.findViewById(R.id.rg_type);
		this.from = (Spinner) view.findViewById(R.id.sp_from);
		this.to = (Spinner) view.findViewById(R.id.sp_to);

		return view;
	}

	/**
	 * Callback function for the OnClickListener interface.
	 */
	@Override
	public void onClick(View v) {
		this.activity.onSubmit();
	}
	
	public String getName() {
		return name.getText().toString();
	}
	
	public int getType() {
		int type = -1;
		switch (typeContainer.getCheckedRadioButtonId()) {
		case R.id.type_0:
			type = 0;
			break;
		case R.id.type_1:
			type = 1;
			break;
		case R.id.type_2:
			type = 2;
			break;	
		}
		return type;
	}
	
	public String getFrom() {
		return String.valueOf(from.getSelectedItem());
	}
	
	public String getTo() {
		return String.valueOf(to.getSelectedItem());
	}
		
}
