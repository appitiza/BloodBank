package net.appitiza.android.lifedrop.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.constants.Constants;
import net.appitiza.android.lifedrop.ui.callbacks.ValidationDialogCallback;


public class BaseFragment extends Fragment {


    protected void moveToNextActivity(boolean shouldfinish, Class targetactivty, Bundle bundle) {
        Intent intent = new Intent(getActivity(), targetactivty);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (shouldfinish && getActivity() != null) {
            getActivity().finish();
        }

    }

    protected void moveToNextActivity(Class targetactivty, Bundle bundle, View v, String transitionName) {
        try {
            Intent mIntent = new Intent(getActivity(), targetactivty);
            if (bundle != null) {
                mIntent.putExtras(bundle);
            }
         //  String transitionName = getString(R.string.transition);

            if(getActivity() != null) {
                ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                v,   // Starting view
                                transitionName    // The String
                        );

                ActivityCompat.startActivity(getActivity(), mIntent, options.toBundle());
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void showNetworkAlert() {
        final AlertDialog mAlert = new AlertDialog.Builder(getActivity()).create();
        mAlert.setTitle("Network Alert");
        mAlert.setMessage("Unable to connect");
        mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlert.dismiss();
            }
        });
        mAlert.show();

    }
    public void showAlert(String heading, String message) {
        final AlertDialog mAlert = new AlertDialog.Builder(getActivity()).create();
        mAlert.setTitle(heading);
        mAlert.setMessage(message);
        mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlert.dismiss();
            }
        });
        mAlert.show();

    }
    public void showAlert(String heading, String message, final ValidationDialogCallback callback, final View v,final int dialogid) {
        final AlertDialog mAlert = new AlertDialog.Builder(getActivity()).create();
        mAlert.setTitle(heading);
        mAlert.setMessage(message);
        mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlert.dismiss();
                if(callback != null)
                {
                    callback.onOkClick(v,dialogid);
                }
            }
        });
        mAlert.show();

    }
    public void showAlert(String heading, String message, final ValidationDialogCallback callback, final View v) {
        final AlertDialog mAlert = new AlertDialog.Builder(getActivity()).create();
        mAlert.setTitle(heading);
        mAlert.setMessage(message);
        mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlert.dismiss();
                if(callback != null)
                {
                    callback.onOkClick(v, Constants.VALIDATION_DIALOG_ID);
                }
            }
        });
        mAlert.show();

    }
    public void showUnexpectedError() {
        final AlertDialog mAlert = new AlertDialog.Builder(getActivity()).create();
        mAlert.setTitle(getString(R.string.error));
        mAlert.setMessage(getString(R.string.unexpected_error));
        mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlert.dismiss();
            }
        });
        mAlert.show();

    }
}
