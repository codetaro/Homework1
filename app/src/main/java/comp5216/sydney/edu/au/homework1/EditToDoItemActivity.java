package comp5216.sydney.edu.au.homework1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;


public class EditToDoItemActivity extends Activity {
	public int position=0;
	EditText etItem; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//populate the screen using the layout
		setContentView(R.layout.activity_edit_item);
		
		//Get the data from the main screen
		String itemJson = getIntent().getStringExtra("item");

		if (itemJson != null) {
			ToDoItem editItem = new Gson().fromJson(itemJson, ToDoItem.class);
			position = getIntent().getIntExtra("position",-1);

			// show original content in the text field
			etItem = (EditText)findViewById(R.id.etEditItem);
			etItem.setText(editItem.todo);
		}
	}

	public void onSubmit(View v) {
	  etItem = (EditText) findViewById(R.id.etEditItem);
	  
	  // Prepare data intent for sending it back
	  Intent data = new Intent();
	  
	  // Pass relevant data back as a result
//	  data.putExtra("item", etItem.getText().toString());
	  ToDoItem item = new ToDoItem(etItem.getText().toString());
	  data.putExtra("item", new Gson().toJson(item));
	  data.putExtra("position", position);


	  // Activity finished ok, return the data
	  setResult(RESULT_OK, data); // set result code and bundle data for response
	  finish(); // closes the activity, pass data to parent
	}

	public void onCancel(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_cancel_title)
				.setMessage(R.string.dialog_cancel_msg)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// discard the edit and return to main activity
						Intent data = new Intent();
						setResult(RESULT_CANCELED, data);
						finish();
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//User cancelled the dialog
						//nothing happens
					}
				});

		builder.create().show();
	}
}
