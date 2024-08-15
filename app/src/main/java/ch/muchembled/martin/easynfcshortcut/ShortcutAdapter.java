package ch.muchembled.martin.easynfcshortcut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.muchembled.martin.easynfcshortcut.model.Shortcut;

public class ShortcutAdapter extends RecyclerView.Adapter<ShortcutAdapter.ShortcutViewHolder> {

    private List<Shortcut> shortcuts;
    private View.OnClickListener onAddClickListener;
    private OnShortcutClickedListener onShortcutClickedListener;

    public ShortcutAdapter(List<Shortcut> shortcuts, View.OnClickListener onAddClickedListener, OnShortcutClickedListener onShortcutClickedListener){
        this.shortcuts = shortcuts;
        this.onAddClickListener = onAddClickedListener;
        this.onShortcutClickedListener = onShortcutClickedListener;
    }

    @NonNull
    @Override
    public ShortcutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_shortcut, parent, false);
        return new ShortcutViewHolder(holderLayout, onShortcutClickedListener, onAddClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortcutViewHolder holder, int position) {
        if(position == shortcuts.size()){
            holder.setAsAddButton();
        }else{
            holder.resetVisibility();
            holder.shortcutName.setText(shortcuts.get(position).getName());
            holder.tagId.setText(shortcuts.get(position).getTagId());
            holder.shortcutTypeText.setText(shortcuts.get(position).getType());
            holder.shortcutTypeImage.setImageResource(shortcuts.get(position).getType() == Shortcut.ShortcutType.App.name() ? R.drawable.baseline_android : R.drawable.baseline_blur);
            holder.shortcutUrlText.setText(shortcuts.get(position).getUrlToOpen());
        }
    }

    public void setShortcuts(List<Shortcut> shortcuts) {
        this.shortcuts = shortcuts;
    }

    @Override
    public int getItemCount() {
        return shortcuts.size() + 1;
    }

    class ShortcutViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView shortcutName;
        private TextView tagId;
        private ImageView shortcutTypeImage;
        private TextView shortcutTypeText;
        private TextView shortcutUrlText;
        private ImageView shortcutUrlImage;
        private ImageView addButton;
        private View.OnClickListener onAddClickListener;

        public ShortcutViewHolder(@NonNull View itemView, OnShortcutClickedListener onShortcutClickedListener, View.OnClickListener onAddClickedListener) {
            super(itemView);
            this.itemView = itemView;
            this.shortcutName = itemView.findViewById(R.id.viewholder_shortcut_name);
            this.shortcutTypeImage = itemView.findViewById(R.id.viewholder_shortcut_type_image);
            this.shortcutTypeText = itemView.findViewById(R.id.viewholder_shortcut_type_text);

            this.tagId = itemView.findViewById(R.id.viewholder_shortcut_tag_id);
            this.shortcutUrlText = itemView.findViewById(R.id.viewholder_shortcut_url_text);
            this.shortcutUrlImage = itemView.findViewById(R.id.viewholder_shortcut_url_image);
            this.addButton = itemView.findViewById(R.id.viewholder_shortcut_add_button);

            this.onAddClickListener = onAddClickedListener;

        }

        public void setAsAddButton() {
            itemView.setOnClickListener(this.onAddClickListener);
            shortcutName.setVisibility(View.INVISIBLE);
            shortcutTypeText.setVisibility(View.INVISIBLE);
            shortcutTypeImage.setVisibility(View.INVISIBLE);
            shortcutUrlText.setVisibility(View.INVISIBLE);
            shortcutUrlImage.setVisibility(View.INVISIBLE);
            tagId.setVisibility(View.INVISIBLE);
            addButton.setVisibility(View.VISIBLE);
        }

        public void resetVisibility(){
            shortcutName.setVisibility(View.VISIBLE);
            shortcutTypeText.setVisibility(View.VISIBLE);
            shortcutTypeImage.setVisibility(View.VISIBLE);
            shortcutUrlText.setVisibility(View.VISIBLE);
            shortcutUrlImage.setVisibility(View.VISIBLE);
            tagId.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.INVISIBLE);
        }

    }

    public interface OnShortcutClickedListener{
        void onShortcutClicked(View v, Shortcut shortcut);
    }
}
