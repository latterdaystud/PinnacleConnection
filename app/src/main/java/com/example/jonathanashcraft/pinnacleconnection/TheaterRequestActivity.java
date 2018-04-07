package com.example.jonathanashcraft.pinnacleconnection;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TheaterRequestActivity extends AppCompatActivity {

    private Button start;
    private Button end;
    private Button save;
    private Button reserve;
    private Button submitRes;
    private ListView unavialableListView;
    private ListView userReservedListView;
    private TextView savedTime;
    private CalendarView calendar;
    private TextView selectedDate;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private ChildEventListener calendarListener;
    private DatabaseReference calendarRef;
    private TheaterReservation res;
    private CustomCalendarAdapter unavailableAdapter;
    private CustomCalendarAdapter userReservedAdapter;
    private ArrayList <TheaterReservation> calendarList;
    private ArrayList <TheaterReservation> reservedList;
    private Dialog myDialog;

    /**
     * Contains the class and layout of the theater request activity.
     * @author Jonathan Ashcraft
     * @param savedInstanceState instance of the phone
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_request);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.pinnacle_logo);
        setTitle(" Reserve Theater");
        Calendar cal = Calendar.getInstance();
        // Create the start time picker for later reference in reservations.
        final TimePickerDialog startTime = new TimePickerDialog(TheaterRequestActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                if (i < 8) {
                    Snackbar.make(getCurrentFocus(), "Cannot reserve theater between 12:00am to 8:00am", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                String time = i + ":" + i1;
                start.setText(time);
            }
        },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);

        // Create the time picker for the end date and time for reference by the reservation dialog box later.
        final TimePickerDialog endTime = new TimePickerDialog(TheaterRequestActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String[] tokens = start.getText().toString().split(":");
                // does not set it if invalid.
                if(tokens.length == 1) {
                    Snackbar.make(getCurrentFocus(), "Please enter a start time", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                else if (i < Integer.parseInt(tokens[0]) || (i == Integer.parseInt(tokens[0]) && i1 <= Integer.parseInt(tokens[1]))) {
                    Snackbar.make(getCurrentFocus(), "Your end time cannot be before your start time", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                String time = i + ":" + i1;
                end.setText(time);
            }
        },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);

        // Create the dialog to be used.
        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.theater_reservation_popup);
        myDialog.setCancelable(false);

        Window window = myDialog.getWindow();
        if (window != null) {
            window.setLayout(400, 500);
            window.setBackgroundDrawableResource(android.R.color.darker_gray);
        }

        // FireBase stuff!
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        calendarRef = database.getReference().child("Reservations");

        // Set the private variables to the view boxes.
        calendarList = new ArrayList<TheaterReservation>();
        reservedList = new ArrayList<>();
        unavailableAdapter = new CustomCalendarAdapter(this, calendarList, false);
        userReservedAdapter = new CustomCalendarAdapter(this, reservedList, true);

        // Set the variables for the activity layout.
        unavialableListView = myDialog.findViewById(R.id.unavailable_list);
        userReservedListView = findViewById(R.id.userReserved);
        unavialableListView.setAdapter(unavailableAdapter);
        userReservedListView.setAdapter(userReservedAdapter);
        calendar = findViewById(R.id.calendarView);
        selectedDate = findViewById(R.id.date_selected);
        savedTime = myDialog.findViewById(R.id.saved_time);
        start = myDialog.findViewById(R.id.Start);
        end = myDialog.findViewById(R.id.End);
        save = myDialog.findViewById(R.id.save);
        submitRes = myDialog.findViewById(R.id.submit_res);
        reserve = findViewById(R.id.button_reserve);

        // Set the textbox to the current date when no date is selected.
        SimpleDateFormat df = new SimpleDateFormat("M/dd/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        selectedDate.setText(date);

        // On click for the calendar
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = (i1+1) + "/" + i2 + "/" + i;
                selectedDate.setText(date);
            }
        });

        // When reserve is pressed, open the dialog box.
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpDisplay();
                unavailableAdapter.myList.clear();
                myDialog.show();
                Log.i("Open dialog","Dialog view should be open");
                // When back is pressed then close and clear data.
                myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            // Clear the input values.
                            savedTime.setText("");
                            start.setText("Start Time");
                            end.setText("End Time");
                            unavailableAdapter.myList.clear();
                            dialog.cancel();
                            Log.i("Close dialog","Dialog view should be closed");
                            return true;
                        }
                        return false;
                    }
                });
                // On the on click of start or end time, open the time clickers.
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startTime.show();
                    }
                });
                end.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        endTime.show();
                    }
                });

                //On save will be the bulk of where we check whether or not the time is valid and available.
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // For the purpose of testing.
                        String[] tokens = start.getText().toString().split(":");
                        String[] tokens2 = end.getText().toString().split(":");

                        // Exit if times are invalid
                        if (tokens.length == 1 || tokens2.length == 1){
                            Snackbar.make(view, "You must enter the time frame", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            return;
                        }

                        // Now begins the testing.
                        String timeSaved = start.getText().toString() + " - " + end.getText().toString()
                                + " " + selectedDate.getText().toString();
                        String timeStart = start.getText().toString() + " " + selectedDate.getText().toString();
                        String timeEnd = end.getText().toString() + " " + selectedDate.getText().toString();
                        SimpleDateFormat str = new SimpleDateFormat("h:m MM/dd/yyyy");
                        Date strDate = null;
                        Date endDate = null;
                        Calendar tomorrowDate = (Calendar.getInstance());
                        tomorrowDate.add(Calendar.MINUTE, 60);
                        Date currentDate = tomorrowDate.getTime();

                        // Set the end and start date.
                        try {
                            endDate = str.parse(timeEnd);
                            strDate = str.parse(timeStart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // If date is before current date then display error message.
                        if (currentDate.getTime() > strDate.getTime()) {
                            Snackbar.make(view, "You can only reserve for future times", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else if (timeUnavailable(strDate, endDate)) { // Check if time is unavailable using the function timeUnavailable.
                            Snackbar.make(view, "Your time overlaps a time already reserved", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else {
                            savedTime.setText(timeSaved);
                            res = new TheaterReservation(strDate, endDate, CurrentUser.getCurrentUser());
                        }
                    }
                });

                // When submit is pressed then save to firebase and clear.
                submitRes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(res != null) {
                            // set the value in the Calendar firebase.
                            calendarRef.child(res.getID()).setValue(res);

                            // Add to the user list.
                            userReservedAdapter.add(res);
                            userReservedAdapter.notifyDataSetChanged();
                            res = null;

                            // Clear the input values.
                            savedTime.setText("");
                            start.setText("Start Time");
                            end.setText("End Time");
                            Toast.makeText(getApplicationContext(), "Time Reserved", Toast.LENGTH_SHORT / 3).show();

                            // Clear the dialog box of its list view.
                            unavailableAdapter.myList.clear();
                            myDialog.cancel();
                            myDialog.onBackPressed();
                            Log.i("Close dialog","Dialog view should be closed");
                        }
                        else {
                            Snackbar.make(view, "Please save a valid reservation", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * Check to see if the dates overlap other already reserved dates.
     * @param start time
     * @param end time
     * @return if unavailable
     */
    private Boolean timeUnavailable(Date start, Date end) {
        for (int i = 0; i < unavailableAdapter.getCount(); i++) {
            TheaterReservation tempRes = unavailableAdapter.getItem(i);

            // Check if the times overlap.
            if ((start.before(tempRes.getStartTime()) && end.after(tempRes.getStartTime()))
                    || (start.after(tempRes.getStartTime()) && start.before(tempRes.getEndTime()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helps with the display of unavailable times from firebase.
     */
    private void setUpDisplay() {
        calendarListener = new ChildEventListener() {
            // Add the child added to a tempAnnouncement

            /**
             * Sets up what happens when a value is received from firebase.
             * @param dataSnapshot The link to the data.
             * @param s not used.
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Get the date
                TheaterReservation resTemp;
                resTemp = dataSnapshot.getValue(TheaterReservation.class);
                // Format it.
                SimpleDateFormat str = new SimpleDateFormat("M/dd/yyyy");
                String selectedTime = str.format(resTemp.getStartTime());

                // If object exists and is on the same day as day selected them add it to times selected.
                if (resTemp != null && Objects.equals(selectedTime, selectedDate.getText().toString())) {
                    unavailableAdapter.add(resTemp);
                    unavailableAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Failed to read", "No theater reservation found");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // What is going to happen if the child changes
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // What are we going to do if the child is removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // What happens if we move the child
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // What if we cancel... what....
            }
        };
        calendarRef.addChildEventListener(calendarListener);
    }

    /**
     * Contains the format for the custom Calendar time adapter.
     * It displays them differently based on if it should display date.
     */
    public class CustomCalendarAdapter extends BaseAdapter {

        ArrayList<TheaterReservation> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;
        Boolean displayDate;


        public CustomCalendarAdapter(Context context, ArrayList<TheaterReservation> myList, Boolean displayTime) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(this.context);
            this.displayDate = displayTime;

        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public TheaterReservation getItem(int i) {
            return myList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        /**
         * This displays the reservations in the correct format.
         * @param position Position of the specific variable.
         * @param convertView not used
         * @param viewGroup not used
         * @return the created view.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.reserved_time, null);
            TheaterReservation theRes = myList.get(position);
            SimpleDateFormat str;
            if (displayDate) {
                str = new SimpleDateFormat("hh:mma MM/dd/yyyy");
            } else {
                str = new SimpleDateFormat("hh:mma");
            }
            String startTime = str.format(theRes.getStartTime());
            String endTime = str.format(theRes.getEndTime());

            // set texviews to be edited.
            TextView start = view.findViewById(R.id.start_res);
            TextView end = view.findViewById(R.id.end_res);

            // Set the text.
            start.setText(startTime);
            end.setText(endTime);

            return view;
        }

        public void add(TheaterReservation theaterReservation) {
            myList.add(theaterReservation);
        }
    }

}
