package in.reweyou.reweyou;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by master on 20/11/16.
 */

public abstract class AlertDialogBox {

    private AlertDialog.Builder dialog;

    public AlertDialogBox(Activity context, String title, String message, String positive, String negative) {
        dialog = new AlertDialog.Builder(context);
        if (title != null)
            dialog.setTitle(title);
        if (message != null) {
            dialog.setMessage(message);
        }
        if (positive != null)
            dialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onPositiveButtonClick(dialog);
                }
            });

        if (negative != null)
            dialog.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onNegativeButtonClick(dialog);
                }
            });
    }


    public abstract void onNegativeButtonClick(DialogInterface dialog);

    public abstract void onPositiveButtonClick(DialogInterface dialog);

    public void show() {
        dialog.show();
    }

    public void setCancellable(boolean cancellable) {
        dialog.setCancelable(cancellable);
    }
}
