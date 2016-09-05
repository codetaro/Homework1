package comp5216.sydney.edu.au.homework1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //define variables
    ListView listview;
    ArrayList<ToDoItem> items;
    UsersAdapter itemsAdapter;
    EditText addItemEditText;

    public final int EDIT_ITEM_REQUEST_CODE = 647;
    public final int ADD_ITEM_REQUEST_CODE = 547;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);

        //reference the "listview" variable to the id-"listview" in the layout
        listview = (ListView) findViewById(R.id.listview);
        addItemEditText = (EditText) findViewById(R.id.txtNewItem);

        //create an ArrayList of ToDoItem
        items = new ArrayList<ToDoItem>();
        items.add(new ToDoItem("item one"));
        items.add(new ToDoItem("item two"));

        //must call it before creating the adapter, because it references the right item list
//        readItemsFromFile();
        readItemsFromDatabase();

        //create an adapter for the list view using Android's built-in item layout
        itemsAdapter = new UsersAdapter(this, items);

        //connect the listview and the adapter
        listview.setAdapter(itemsAdapter);

        setupListViewListener();
    }

    public void onAddItemClick(View view) {
        /*String toAddString = addItemEditText.getText().toString();
        if (toAddString != null && toAddString.length() > 0) {
            itemsAdapter.add(new ToDoItem(toAddString));
            addItemEditText.setText("");

//            saveItemsToFile();
            saveItemsToDatabase();
        }*/
        Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
        if (intent != null) {
            // put "extras" into the bundle for access in the edit activity
//            intent.putExtra("item", updateItem);
//            intent.putExtra("position", position);
            // brings up the second activity
//            startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
            itemsAdapter.notifyDataSetChanged();
        }
    }

    private void setupListViewListener() {
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long rowId)
            {
//                String updateItem = (String) itemsAdapter.getItem(position);
                Log.i("MainActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //delete the item
                                items.remove(position);
                                itemsAdapter.notifyDataSetChanged();

//                                saveItemsToFile();
                                saveItemsToDatabase();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //User cancelled the dialog
                                //nothing happens
                            }
                        });

                builder.create().show();
                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ToDoItem updateItem = (ToDoItem) itemsAdapter.getItem(position);
                Log.i("MainActivity", "Clicked item " + position + ": " + updateItem);

                Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the edit activity
                    intent.putExtra("item", new Gson().toJson(updateItem));
                    intent.putExtra("position", position);
                    // brings up the second activity
                    startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
                    itemsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ITEM_REQUEST_CODE && resultCode != RESULT_CANCELED) {
            // Extract name value from result extras
            String itemJson = data.getExtras().getString("item");
            ToDoItem editedItem = new Gson().fromJson(itemJson, ToDoItem.class);
            int position = data.getIntExtra("position", -1);
            items.set(position, editedItem);
            Log.i("Updated Item in list:", editedItem + ",position:"
                    + position);

            Toast.makeText(this, "updated:" + editedItem, Toast.LENGTH_SHORT).show();
            itemsAdapter.notifyDataSetChanged();

//            saveItemsToFile();
            saveItemsToDatabase();
        } else if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode != RESULT_CANCELED) {    // && short-circuits, whereas & doesn't
            String itemJson = data.getExtras().getString("item");
            ToDoItem item = new Gson().fromJson(itemJson, ToDoItem.class);
            itemsAdapter.add(item);
            itemsAdapter.notifyDataSetChanged();

            saveItemsToDatabase();
        }
    }

    /*private void readItemsFromFile() {
        //retrieve the app's private folder.
        //this folder cannot be accessed by other apps
        File filesDir = getFilesDir();

        //prepare a file to read the data
        File todoFile = new File(filesDir, "todo.txt");

        //if file does not exist, create an empty list
        if (!todoFile.exists()) {
            items = new ArrayList<String>();
        } else {
            try {
                //read data and put it into the ArrayList
                items = new ArrayList<String>(FileUtils.readLines(todoFile));
            } catch (IOException ex) {
                items = new ArrayList<String>();
            }
        }
    }*/

    /*private void saveItemsToFile() {
        File filesDir = getFilesDir();
        //using the same file for reading. Should use define a global string instead.
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }*/

    private void readItemsFromDatabase() {
        //read items from database
        List<ToDoItem> itemsFromORM = ToDoItem.listAll(ToDoItem.class);
        items = new ArrayList<ToDoItem>();
        if (itemsFromORM != null & itemsFromORM.size() > 0) {
            for (ToDoItem item : itemsFromORM) {
                items.add(item);
            }
        }
    }

    private void saveItemsToDatabase() {
        ToDoItem.deleteAll(ToDoItem.class);
        for (ToDoItem todo : items) {
//            ToDoItem item = new ToDoItem(todo);
//            item.save();

            todo.save();
        }
    }
}
