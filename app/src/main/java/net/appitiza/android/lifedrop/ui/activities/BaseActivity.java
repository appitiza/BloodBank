package net.appitiza.android.lifedrop.ui.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.constants.Constants;
import net.appitiza.android.lifedrop.ui.callbacks.ValidationDialogCallback;


public class BaseActivity extends AppCompatActivity {


    protected void moveToNextActivity(boolean shouldfinish, Class targetactivty, Bundle bundle) {
        Intent intent = new Intent(this, targetactivty);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (shouldfinish) {
            this.finish();
        }

    }
    protected void moveToNextActivityWithFade(boolean shouldfinish, Class targetactivty, Bundle bundle) {


        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out);

        Intent intent = new Intent(this, targetactivty);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent,options.toBundle());
        if (shouldfinish) {
            this.finish();
        }

    }
    protected void moveToNextActivity(Class targetactivty, Bundle bundle, View v, String transitionName) {
        try {
            Intent mIntent = new Intent(this, targetactivty);
            if (bundle != null) {
                mIntent.putExtras(bundle);
            }

            ActivityOptionsCompat options =

                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            v,   // Starting view
                            transitionName    // The String
                    );

            ActivityCompat.startActivity(this, mIntent, options.toBundle());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void showNetworkAlert() {
        final AlertDialog mAlert = new AlertDialog.Builder(this).create();
        mAlert.setTitle(getString(R.string.network_alert));
        mAlert.setMessage(getString(R.string.unable_connect));
        mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlert.dismiss();
            }
        });
        mAlert.show();

    }

    public void showAlert(String heading, String message, final ValidationDialogCallback callback,final View v,final int dialogid) {
        final AlertDialog mAlert = new AlertDialog.Builder(this).create();
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
    public void showAlert(String heading, String message, final ValidationDialogCallback callback,final View v) {
        final AlertDialog mAlert = new AlertDialog.Builder(this).create();
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
    public void showAlert(String heading, String message) {
        final AlertDialog mAlert = new AlertDialog.Builder(this).create();
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
    public void showUnexpectedError() {
        final AlertDialog mAlert = new AlertDialog.Builder(this).create();
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
