package ch.muchembled.martin.easynfcshortcut.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Model of a shortcut
 */
@Entity
public class Shortcut {

    private static final String TYPE_APP_STRING = "App";
    private static final String TYPE_URL_STRING = "Website";

    @PrimaryKey @NonNull
    private String tagId;
    @ColumnInfo(name="name")
    private String name;
    @ColumnInfo(name="type")
    private String type;
    @ColumnInfo(name="creationDate")
    private String creationDate;
    @ColumnInfo(name="lastCallDate")
    private String lastCallDate;
    @ColumnInfo(name="urlToOpen")
    private String urlToOpen;

    public Shortcut(String tagId, String name, ShortcutType type, String creationDate, String lastCallDate, String urlToOpen){
        this.tagId = tagId;
        this.name = name;
        this.type = type == ShortcutType.App ? TYPE_APP_STRING : TYPE_URL_STRING;
        this.creationDate = creationDate; // Set automatically
        this.lastCallDate = lastCallDate; // Set automatically
        this.urlToOpen = urlToOpen;
    }

    public Shortcut(String tagId, String name, String type, String creationDate, String lastCallDate, String urlToOpen){
        this.tagId = tagId;
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
        this.lastCallDate = lastCallDate;
        this.urlToOpen = urlToOpen;
    }

    @NonNull
    public String getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getLastCallDate() {
        return lastCallDate;
    }

    public String getUrlToOpen() {
        return urlToOpen;
    }

    public enum ShortcutType{
        App(),
        Website();
    }

    public Intent generateIntent(Context context){

        if(type.equals(ShortcutType.App.name())){
            PackageManager manager = context.getPackageManager();
            return manager.getLaunchIntentForPackage(urlToOpen);
        }
        if(type.equals(ShortcutType.Website.name())){
            return new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
        }

        return null;

    }
}
