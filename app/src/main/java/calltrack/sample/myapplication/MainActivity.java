package calltrack.sample.myapplication;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import calltrack.sample.R;
import calltrack.sample.myapplication.Server.AppPermissions;
import calltrack.sample.myapplication.Server.Constants;
import calltrack.sample.myapplication.Server.PrefStore;

public class MainActivity extends AppPermissions {
    EditText inbountET, pickedupET, callendET;
    Button saveBT;
    PrefStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inbountET = (EditText) findViewById(R.id.inbountET);
        pickedupET = (EditText) findViewById(R.id.pickedupET);
        callendET = (EditText) findViewById(R.id.callendET);
        saveBT = (Button) findViewById(R.id.submitBT);
        checkPermissions();
        store = new PrefStore(this);
        if(store.contains(Constants.INBOUND_URL))
            inbountET.setText(store.getString(Constants.INBOUND_URL));
        if(store.contains(Constants.PICKEDUP_URL))
            pickedupET.setText(store.getString(Constants.PICKEDUP_URL));
        if(store.contains(Constants.CALLEDND_URL))
            callendET.setText(store.getString(Constants.CALLEDND_URL));

        saveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inboundUrl = inbountET.getText().toString().trim();
                String pickedupUrl = pickedupET.getText().toString().trim();
                String callendUrl = callendET.getText().toString().trim();
                store.saveString(Constants.INBOUND_URL, inboundUrl);
                store.saveString(Constants.PICKEDUP_URL, pickedupUrl);
                store.saveString(Constants.CALLEDND_URL, callendUrl);
                Toast.makeText(getApplicationContext(), "Webhook address updated successfully", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Toast.makeText(this, "Permissions Granted.", Toast.LENGTH_LONG).show();
    }

    private void checkPermissions() {
        MainActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE,Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.CALL_PHONE,Manifest.permission.CALL_PRIVILEGED,Manifest.permission.INTERNET},R.string.app_name
                , 123);
    }
}
