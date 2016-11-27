package in.reweyou.reweyou;

/**
 * Created by Reweyou on 12/20/2015.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import in.reweyou.reweyou.classes.UserSessionManager;

/**
 * Created by Belal on 11/22/2015.
 */
public class Upload2 {


    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/reporting.php";
    private final String token1;
    private final String deviceid;


    private int serverResponseCode;

    public Upload2(PostReport postReport) {
        UserSessionManager userSessionManager = new UserSessionManager(postReport);
        this.token1 = userSessionManager.getKeyAuthToken();
        this.deviceid = userSessionManager.getDeviceid();
    }

    public String uploadVideo(String fileName, String filePath, String headline, String edittag, String category, String description, String place, String address, String time, String encodedimage, boolean image, boolean video, boolean gif, String number, String username, String token) {

        Log.d("eee", headline + "  " + edittag + "   " + category + "   " + description + "    " + place + "    " + "    " + address + "   " + time + "    " + username);

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 5 * 1024 * 1024;

        File sourceFile = new File(filePath);
        Log.d("sizeeee", String.valueOf(Integer.parseInt(String.valueOf(sourceFile.length() / (1024)))));

        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("myFile", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //Adding Headline

            if (headline != null) {
                dos.writeBytes("Content-Disposition: form-data; name=\"head\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(headline);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
            }

            //Adding NAME


            dos.writeBytes("Content-Disposition: form-data; name=\"headline\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(description);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"token\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(token1);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"deviceid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(deviceid);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //Adding tag

            dos.writeBytes("Content-Disposition: form-data; name=\"tag\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(edittag);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //Adding number

            dos.writeBytes("Content-Disposition: form-data; name=\"number\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("7054392300");
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            dos.writeBytes("Content-Disposition: form-data; name=\"name\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(username);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"time\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(time);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            if (encodedimage != null) {
                dos.writeBytes("Content-Disposition: form-data; name=\"image\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(encodedimage);
                dos.writeBytes(lineEnd);
            }

            //Adding location

            dos.writeBytes("Content-Disposition: form-data; name=\"location\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(place);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //Adding tag
            dos.writeBytes("Content-Disposition: form-data; name=\"type\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(category);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //Adding Parameter address

            dos.writeBytes("Content-Disposition: form-data; name=\"address\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(address);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //Adding Parameter image

            if (image) {
                dos.writeBytes("Content-Disposition: form-data; name=\"report\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("image");
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
            }

            if (video) {
                Log.d("test2333", "true");
                dos.writeBytes("Content-Disposition: form-data; name=\"report\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("video");
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
            }

            if (gif) {
                Log.d("test2333gif", "true");

                dos.writeBytes("Content-Disposition: form-data; name=\"report\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("gif");
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
            }


            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();


            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Huzza", "Malinformed");
        } catch (Exception e) {
            // e.printStackTrace();
            Log.e("Huzza", Log.getStackTraceString(e));
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            return sb.toString();
        } else {
            return "Could not upload";
        }
    }
}
