package ch.muchembled.martin.easynfcshortcut;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ch.muchembled.martin.easynfcshortcut.database.ShortcutDatabase;
import ch.muchembled.martin.easynfcshortcut.model.Shortcut;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Shortcut> shortcuts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.d("main", "Creating views");

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        shortcuts = ShortcutDatabase.getInstance(this).shortcutDao().getAllShortcuts();

        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        onIntent(getIntent());
        shortcuts = ShortcutDatabase.getInstance(this).shortcutDao().getAllShortcuts();


    }

    private void onIntent(Intent intent){

        if(
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                        NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                        NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
        ){
            intent.setAction(null);
            Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            StringBuilder builder = new StringBuilder("#");
            for(byte b : tag.getId()){
                builder.append(String.format("%02x", b));
            }
            String tagId = builder.toString();
            boolean exist = false;
            for(Shortcut s : shortcuts){
                if(s.getTagId().equals(tagId)){
                    exist = true;
                    Log.d("main", "onIntent: " + (getPackageManager().getLaunchIntentForPackage("com.instagram.android") == null));
                    startActivity(s.generateIntent(this));
                    finish();
                }
            }

            if(!exist){
                Intent addActivityIntent = new Intent(this, AddShortcutActivity.class);
                addActivityIntent.putExtra(AddShortcutActivity.IS_TAG_KNOWN_KEY, true);
                addActivityIntent.putExtra(AddShortcutActivity.TAG_KEY, tagId);
                startActivity(addActivityIntent);
            }
        }
    }

    private void initViews(){
        Log.d("main", "initViews: " + shortcuts.size());
        this.recyclerView = findViewById(R.id.main_activity_recycler_view);

        recyclerView.setAdapter(new ShortcutAdapter(
                shortcuts,
                (View v) -> {
                    Intent intent = new Intent(this, AddShortcutActivity.class);
                    intent.putExtra(AddShortcutActivity.IS_TAG_KNOWN_KEY, false);
                    startActivity(intent);
                },
                (View v, Shortcut shortcut) -> {
                    // On shortcut clicked
                }
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}