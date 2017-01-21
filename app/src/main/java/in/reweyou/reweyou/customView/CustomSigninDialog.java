package in.reweyou.reweyou.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.Signup;

/**
 * Created by master on 21/1/17.
 */

public class CustomSigninDialog {

    private final Context mContext;
    private final AlertDialog alertDialog1;

    public CustomSigninDialog(Context context) {
        this.mContext = context;

        LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_signin, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        // Include dialog.xml file
        dialog.setView(confirmDialog);
        alertDialog1 = dialog.create();

        // Set dialog title
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog1.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button button = (Button) confirmDialog.findViewById(R.id.buttonConfirm);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
                mContext.startActivity(new Intent(mContext, Signup.class));
            }
        });

    }

    public void show() {
        alertDialog1.show();
    }
}
