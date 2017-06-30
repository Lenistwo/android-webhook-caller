package calltrack.sample.myapplication.Server;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by root on 7/12/16.
 */

public class SyncManager {


    public void send( String myUrl, String number, String name,String line) {


        AsyncHttpClient client = new AsyncHttpClient();

      client.setMaxRetriesAndTimeout(3,20000);

       client.addHeader("Content-Type", "text/plain; charset=UTF-8");
        client.addHeader("user-agent", "MSIE 6.0; Windows NT 5.1");

        try {

            myUrl= myUrl.replace("{phone_number}",number);
            myUrl=myUrl.replace("{caller_name}",name);
            myUrl=myUrl.replace("{line}",line);
            System.out.printf("Full URL=>" + myUrl);

        }catch (Exception e){e.printStackTrace();
            myUrl= myUrl.replace("{phone_number}","Private Number");
            myUrl=myUrl.replace("{caller_name}","Unknown");
            myUrl=myUrl.replace("{line}",line);
        }



           // String val="http://webhook.site/4440279d-3bbb-4540-a481-5778dd864faf?api-key=234234123&amp;number={phone_number}&amp;name={contact_name}&amp;line={line}&amp;se=start";
            client.get(myUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               Log.e("Call tracker", "onSuccess: " );

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Call tracker", "onFailur: " );
            }
        });

    }
}
