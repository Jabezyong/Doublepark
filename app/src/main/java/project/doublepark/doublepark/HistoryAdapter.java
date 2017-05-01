package project.doublepark.doublepark;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jabez on 18/4/2017.
 */

public class HistoryAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    HistoryListModel tempValues=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public HistoryAdapter(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public static class ViewHolder{

        public TextView textDate;
        public TextView textTitle;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.item_list, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.textDate = (TextView) vi.findViewById(R.id.tv_date);
            holder.textTitle=(TextView)vi.findViewById(R.id.tv_title);


            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( HistoryListModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/
            if(tempValues.isSender()){
                holder.textTitle.setText(Html.fromHtml("Your car was blocked by "+"<b>"+tempValues.getRecipient_carplate()+"</b>"));
            }else{
                holder.textTitle.setText(Html.fromHtml("Your car was blocking "+"<b>"+tempValues.getSender_carplate()+"</b>"));
            }
            holder.textDate.setText( tempValues.getDateString() );
//            holder.textTitle.setText( tempValues.getContent() );

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
        }
        int mp = position;
//        if(position == 0 && tempValues != null){
//            final String key= tempValues.getKey();
//            holder.btnNotify = (Button) vi.findViewById(R.id.button_notify);
//            long difference = new Date().getTime()- tempValues.getDate().getTime();
//            //if within 5minutes
//            // then the button is visible and clickable to reply sender.
//            if (difference < 10*60*1000){
//                holder.btnNotify.setVisibility(Button.VISIBLE);
//                holder.btnNotify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FirebaseDatabase database  = FirebaseDatabase.getInstance();
//                        DatabaseReference myRef =
//                                database.getReference()
//                                        .child(ReplyActionReceiver.notificationTAG)
//                                        .child(key);
//                        MyFirebaseNotification noti = new MyFirebaseNotification(Tags.receiveTAG);
//                        DatabaseReference push = myRef.push();
//                        myRef.push().setValue(noti);
//                        holder.btnNotify.setEnabled(false);
//                        Toast.makeText(activity,R.string.notification_send,Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            }else{
//                holder.btnNotify.setVisibility(Button.GONE);
//            }
//
//        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            HistoryActivity sct = (HistoryActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }


}

