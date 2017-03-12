package com.aa105g2.weddingplatform.main.appointment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.place.PlaceListFragment;


public class AppointmentAlertDFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                .setIcon(R.drawable.alert64)
                // Set Dialog Title
                .setTitle("取消訂單?")
                // Set Dialog Message
                .setMessage("確認取消訂單?")

                // Positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                      Fragment  fragment= new PlaceListFragment();
                        switchFragment(fragment);
                        getActivity().setTitle(R.string.text_Place);
                    }
                })

                // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,	int which) {
                        // Do something else
                        dialog.cancel();
                    }
                }).create();
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}