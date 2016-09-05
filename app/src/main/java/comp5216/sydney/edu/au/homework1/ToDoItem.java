package comp5216.sydney.edu.au.homework1;

import com.google.common.base.Strings;
import com.orm.SugarRecord;

import java.util.Date;

public class ToDoItem extends SugarRecord {
    public String todo;
    public Date creationTime;

    public ToDoItem(){}

    public ToDoItem(String ToDo){

        this.todo = ToDo;
        this.creationTime = new Date();
    }
}
