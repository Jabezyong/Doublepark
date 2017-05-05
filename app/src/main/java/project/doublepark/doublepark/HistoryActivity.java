package project.doublepark.doublepark;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class HistoryActivity extends AppCompatActivity {
    ListView list;
    HistoryAdapter adapter;
    public  HistoryActivity CustomListView = null;
    public ArrayList<HistoryListModel> valuesArr = new ArrayList<HistoryListModel>();
    Date currentDate ;
    boolean retriveDoneAsSender = false;
    boolean retriveDoneAsRecipient = false;
    Query senderQuery,recipientQuery;
    ValueEventListener senderListener,recipientListener;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private Button btnNotify;
    private RelativeLayout relativeLayout;
    private TextView tvDate,tvContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("History");
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Loading ");
//        progressDialog.show();
        setContentView(R.layout.activity_history);
        currentDate = Calendar.getInstance().getTime();
        CustomListView = this;

        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        setListData();

        Resources res =getResources();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        list= ( ListView )findViewById( R.id.historyListView );  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter=new HistoryAdapter( CustomListView, valuesArr,res );
        list.setAdapter( adapter );

        setupFontForTitle();

    }
    /****** Function to set data in ArrayList *************/
    public void setListData() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        SharePrefManager manager = SharePrefManager.getInstance(getApplicationContext());
        final String email = manager.getEmail();
        new Thread(new Runnable() {
            @Override
            public void run() {

                senderQuery = reference.child("notification").orderByChild("sender").equalTo(email);
                senderListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot nodeDataSnapshot = iterator.next();
                            String key = nodeDataSnapshot.getKey();
                            MyFirebaseNotification value = nodeDataSnapshot.getValue(MyFirebaseNotification.class);
                            String a = value.sender;
                            String sender_carplate = value.sender_carplate;
                            String recipient_carplate = value.recipient_carplate;
                            Date date = value.date;
                            if (date != null) {

                                HistoryListModel model = new HistoryListModel();
                                long difference = currentDate.getTime() - date.getTime();
                                String ago = TimeAgo.toDuration(difference);

                                model.setDateString(ago);
                                model.setRecipient_carplate(recipient_carplate);
                                model.setDate(date);
                                model.setKey(key);
                                model.setSender(true);
                                valuesArr.add(model);
                            }
                        }
                        retriveDoneAsSender = true;
                        if (retriveDoneAsRecipient) {
                            sortByTime();

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                senderQuery.addValueEventListener(senderListener);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                recipientQuery = reference.child("notification").orderByChild("recipient").equalTo(email);
                recipientListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot nodeDataSnapshot = iterator.next();
                            String key = nodeDataSnapshot.getKey();
                            MyFirebaseNotification value = nodeDataSnapshot.getValue(MyFirebaseNotification.class);
                            String carplate = value.sender_carplate;
                            Date date = value.date;
                            if (date != null) {

                                HistoryListModel model = new HistoryListModel();
                                long difference = currentDate.getTime() - date.getTime();
                                String ago = TimeAgo.toDuration(difference);

                                model.setDateString(ago);
                                model.setSender_carplate(carplate);
                                model.setDate(date);
                                model.setKey(key);
                                model.setSender(false);
                                valuesArr.add(model);
                            }
                        }
                        retriveDoneAsRecipient = true;
                        if (retriveDoneAsSender) {
                            sortByTime();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                };
                recipientQuery.addValueEventListener(recipientListener);
            }
        }).start();
    }
    public void sortByTime(){
        if(valuesArr.size()>0) {
            Collections.sort(valuesArr, new CustomComparator());
            checkActiveNotification();
            adapter.notifyDataSetChanged();
            senderQuery.removeEventListener(senderListener);
            recipientQuery.removeEventListener(recipientListener);
            progressBar.setVisibility(ProgressBar.GONE);
            if(!valuesArr.isEmpty())//check again since remove one data in checkActiveNotification() method
            list.setVisibility(ListView.VISIBLE);
        }else{
            //Show no record at history if there is no any data
            closeProgressDialog();
            NotificationDialog dialog = new NotificationDialog();
            Bundle bundle = new Bundle();
            bundle.putString("NOTIFICATION_MSG"
                    ,"Has no any Record");
            bundle.putString(Tags.FINISH,"FINISH");
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "show");
        }
        closeProgressDialog();
    }

    private void checkActiveNotification() {
        HistoryListModel historyListModel = valuesArr.get(0);
        initialViewForRelativeLayout();
        final String key = historyListModel.getKey();
        long difference = new Date().getTime() - historyListModel.getDate().getTime();
        //if within 5minutes
        // then the button is visible and clickable to reply sender.
        // if not recipient then no nid to show
        if (difference < 10 * 60 * 1000 && !historyListModel.isSender()) {
            valuesArr.remove(0);
            tvContent.setText(Html.fromHtml("Your car is blocking "+"<b>"+historyListModel.getSender_carplate()+"</b>"));
            tvDate.setText( historyListModel.getDateString() );
            btnNotify.setVisibility(Button.VISIBLE);
            btnNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef =
                            database.getReference()
                                    .child(ReplyActionReceiver.notificationTAG)
                                    .child(key);
                    MyFirebaseNotification noti = new MyFirebaseNotification(Tags.receiveTAG);
                    myRef.push().setValue(noti);
                    btnNotify.setEnabled(false);
                    Toast.makeText(
                            HistoryActivity.this, R.string.notification_send, Toast.LENGTH_LONG).show();

                }
            });
            relativeLayout.setVisibility(View.VISIBLE);
        }else{
            relativeLayout.setVisibility(View.GONE);
        }

    }

    private void initialViewForRelativeLayout() {
        btnNotify = (Button) findViewById(R.id.button_notify);
        tvDate = (TextView) findViewById(R.id.tv_list_date);
        tvContent = (TextView) findViewById(R.id.tv_list_content);
        relativeLayout = (RelativeLayout) findViewById(R.id.send_notificaton_layout);
    }

    public void closeProgressDialog(){
        if(progressDialog.isShowing())
        progressDialog.dismiss();
    }
    public void onItemClick(int mPosition)
    {

    }

    private class CustomComparator implements Comparator<HistoryListModel> {
        @Override
        public int compare(HistoryListModel o1, HistoryListModel o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }

    private void setupFontForTitle() {
        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(R.string.history);
        tv.setTextSize(28);
        tv.setTextColor(Color.parseColor("#FFFFFF"));

        Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getResources().getAssets(),"Spork.ttf");
        tv.setTypeface(typeFace);
        SpannableString s = new SpannableString("Double Park");
        s.setSpan(typeFace, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
    }

}
