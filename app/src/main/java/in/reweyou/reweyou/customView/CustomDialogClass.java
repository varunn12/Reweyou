package in.reweyou.reweyou.customView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.Signup;

/**
 * Created by master on 29/11/16.
 */

public class CustomDialogClass extends Dialog {

    public Activity c;
    public Dialog d;
    private EditText otpField;
    private Button confirm;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);

        otpField = (EditText) findViewById(R.id.editTextOtp);
        confirm = (Button) findViewById(R.id.buttonConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpField.getText().toString().trim().length() > 0) {
                    dismiss();
                    ((Signup) c).verifyOtp(otpField.getText().toString());
                } else Toast.makeText(c, "otp cant be empty", Toast.LENGTH_SHORT).show();

            }
        });

    }


}
