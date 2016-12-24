package in.reweyou.reweyou.model;

import com.orm.SugarRecord;

/**
 * Created by master on 27/12/16.
 */

public class Contact extends SugarRecord {

    String number;

    public Contact() {
    }

    public Contact(String number) {
        this.number = number;
    }
}
