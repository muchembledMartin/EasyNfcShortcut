package ch.muchembled.martin.easynfcshortcut;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Image;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;

import ch.muchembled.martin.easynfcshortcut.database.ShortcutDatabase;
import ch.muchembled.martin.easynfcshortcut.model.Shortcut;

public class AddShortcutActivity extends AppCompatActivity {

    public static final String IS_TAG_KNOWN_KEY = "isTagKnown";
    public static final String TAG_KEY = "TagKey";

    // Shortcut state
    private String tagId;
    private String creationDate;
    private Shortcut.ShortcutType type;

    // UI state
    private boolean isTagKnown;

    // UI elements
    private TextView scanMessageTextView;
    private ImageView scanImageView;
    private EditText shortcutNameEditText;
    private TextView shortcutTagId;

    private ImageView shortcutTypeWebsiteImage;
    private TextView shortcutTypeWebsiteTextView;
    private ImageView shortcutTypeAppImage;
    private TextView shortcutTypeAppTextView;

    private ImageView shortcutUrlImage;
    private EditText shortcutUrlEditText;

    private ImageView addShortcutButton;
    private ImageView returnButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shortcut);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        initViews();

        isTagKnown = getIntent().getBooleanExtra(IS_TAG_KNOWN_KEY, false);
        if(isTagKnown){
            tagId = getIntent().getExtras().getString(TAG_KEY);
            showStep2();
        }else{
            showStep1();
        }

        this.type = Shortcut.ShortcutType.Website;
        updateAddButtonState();
        type = Shortcut.ShortcutType.Website;
        updateTypeViews();

    }

    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        StringBuilder builder = new StringBuilder("#");
        for(byte b : tagFromIntent.getId()){
            builder.append(String.format("%02x", b));
        }
        tagId = builder.toString();
        isTagKnown = true;
        showStep2();
    }

    private void initViews(){
        this.scanMessageTextView = findViewById(R.id.activity_add_nfc_message);
        this.scanImageView = findViewById(R.id.activity_add_shortcut_nfc_image);
        this.returnButton = findViewById(R.id.activity_add_shortcut_back);
        this.shortcutNameEditText = findViewById(R.id.activity_add_shortcut_name);
        this.shortcutTypeWebsiteImage = findViewById(R.id.activity_add_shortcut_type_website_image);
        this.shortcutTypeAppImage = findViewById(R.id.activity_add_shortcut_type_app_image);
        this.shortcutTypeWebsiteTextView = findViewById(R.id.activity_add_shortcut_type_website_text);
        this.shortcutTypeAppTextView = findViewById(R.id.activity_add_shortcut_type_app_text);
        this.shortcutTagId = findViewById(R.id.activity_add_shortcut_tag_id);
        this.shortcutUrlEditText = findViewById(R.id.activity_add_shortcut_url_text);
        this.shortcutUrlImage = findViewById(R.id.activity_add_shortcut_url_image);
        this.addShortcutButton = findViewById(R.id.activity_add_shortcut_add);

        this.returnButton.setOnClickListener(onReturnClicked);
        this.shortcutTypeWebsiteImage.setOnClickListener(onWebsiteClicked);
        this.shortcutTypeWebsiteTextView.setOnClickListener(onWebsiteClicked);
        this.shortcutTypeAppTextView.setOnClickListener(onAppClicked);
        this.shortcutTypeAppImage.setOnClickListener(onAppClicked);
        this.shortcutNameEditText.addTextChangedListener(textWatcher);
        this.shortcutUrlEditText.addTextChangedListener(textWatcher);
    }

    private View.OnClickListener onReturnClicked = (View v) -> {
        if(isTagKnown){
            isTagKnown = false;
            showStep1();
        }else{
            finish();
        }
    };

    private View.OnClickListener onAddShortcutClicked = (View v) -> {
        ShortcutDatabase db = ShortcutDatabase.getInstance(this);
        db.shortcutDao().insert(new Shortcut(tagId, shortcutNameEditText.getText().toString(), type, null, null, shortcutUrlEditText.getText().toString()));
        finish();
    };

    private View.OnClickListener onWebsiteClicked = (View v) -> {
        this.type = Shortcut.ShortcutType.Website;
        updateAddButtonState();
        updateTypeViews();
    };

    private View.OnClickListener onAppClicked = (View v) -> {
        this.type = Shortcut.ShortcutType.App;
        updateAddButtonState();
        updateTypeViews();
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateAddButtonState();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void updateAddButtonState(){
        boolean enable = shortcutNameEditText.getText().length() > 0 && shortcutUrlEditText.getText().length() > 0 && checkURL();
        addShortcutButton.setEnabled(enable);
        addShortcutButton.setAlpha(enable ? 1.f : 0.5f);
    }

    private boolean checkURL(){
        if(Shortcut.ShortcutType.App.equals(type)) {
            return getPackageManager().getLaunchIntentForPackage(shortcutUrlEditText.getText().toString()) != null;
        }
        return true;
    }

    private void setNFCDisabledMode(){
        scanImageView.setImageResource(R.drawable.baseline_sensors_off);
        scanMessageTextView.setText(R.string.nfc_disable_message);
    }

    private void setNFCScanMode(){
        scanImageView.setImageResource(R.drawable.baseline_sensors);
        scanMessageTextView.setText(R.string.scan_tag_message);
    }

    private void showStep1(){
        this.shortcutNameEditText.setVisibility(View.INVISIBLE);
        this.shortcutTagId.setVisibility(View.INVISIBLE);
        this.shortcutTypeWebsiteImage.setVisibility(View.INVISIBLE);
        this.shortcutTypeWebsiteTextView.setVisibility(View.INVISIBLE);
        this.shortcutTypeAppTextView.setVisibility(View.INVISIBLE);
        this.shortcutTypeAppImage.setVisibility(View.INVISIBLE);
        this.shortcutUrlImage.setVisibility(View.INVISIBLE);
        this.shortcutUrlEditText.setVisibility(View.INVISIBLE);
        this.addShortcutButton.setVisibility(View.INVISIBLE);

        this.scanImageView.setVisibility(View.VISIBLE);
        this.scanMessageTextView.setVisibility(View.VISIBLE);

        NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            setNFCScanMode();
        }else{
            setNFCDisabledMode();
        }

    }

    private void showStep2(){
        this.shortcutNameEditText.setVisibility(View.VISIBLE);
        this.shortcutTagId.setVisibility(View.VISIBLE);
        this.shortcutTypeWebsiteImage.setVisibility(View.VISIBLE);
        this.shortcutTypeWebsiteTextView.setVisibility(View.VISIBLE);
        this.shortcutTypeAppTextView.setVisibility(View.VISIBLE);
        this.shortcutTypeAppImage.setVisibility(View.VISIBLE);
        this.shortcutUrlImage.setVisibility(View.VISIBLE);
        this.shortcutUrlEditText.setVisibility(View.VISIBLE);
        this.addShortcutButton.setVisibility(View.VISIBLE);

        updateTypeViews();

        shortcutTagId.setText(tagId);

        this.scanImageView.setVisibility(View.INVISIBLE);
        this.scanMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void updateTypeViews(){
        if(Shortcut.ShortcutType.Website.equals(type)){
            shortcutTypeWebsiteTextView.setAlpha(1);
            shortcutTypeWebsiteImage.setImageAlpha(255);
            shortcutTypeWebsiteImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondary)));
            shortcutTypeAppImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            shortcutTypeAppImage.setImageAlpha(127);
            shortcutTypeAppTextView.setAlpha(0.5f);
        }else{
            shortcutTypeAppTextView.setAlpha(1);
            shortcutTypeAppImage.setImageAlpha(255);
            shortcutTypeAppImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondary)));
            shortcutTypeWebsiteImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            shortcutTypeWebsiteImage.setImageAlpha(127);
            shortcutTypeWebsiteTextView.setAlpha(0.5f);
        }
    }


}