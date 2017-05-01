package project.doublepark.doublepark;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by jabez on 13/4/2017.
 */

public class NotificationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle arguments = getArguments();
        builder.setMessage(arguments.getString(Tags.NOTIFICATION_MSG))
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(arguments.get(Tags.FINISH)!=null){
                            getActivity().finish();
                        }
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
