package in.reweyou.reweyou.classes;

import android.content.Intent;

import in.reweyou.reweyou.PermissionsActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by master on 18/11/16.
 */

public class HandleActivityResult {

    public static final int HANDLE_IMAGE = 10;
    public static final int HANDLE_VIDEO = 11;
    private final int REQUEST_CODE = 0;
    private final int REQUEST_TAKE_GALLERY_VIDEO = 5;
    private final int IMAGE = 11;
    private final int VIDEO = 12;
    private int SELECT_FILE = 1;

    public int handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            return 0;
        }

        if (resultCode == RESULT_OK && requestCode == SELECT_FILE && data != null) {
            return HANDLE_IMAGE;
        }

        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == RESULT_OK) {
            return HANDLE_VIDEO;
        }
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            return 0;
        }
        return requestCode;
    }
}
