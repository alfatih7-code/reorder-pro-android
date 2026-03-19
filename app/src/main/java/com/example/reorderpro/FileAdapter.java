package com.example.reorderpro;

import android.graphics.Color;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private final List<FileItem> fileList = new ArrayList<>();

    // ألوان "النيون" الاحترافية للهوية البصرية
    private final int colorVideo = Color.parseColor("#38BDF8");  // أزرق سماوي
    private final int colorImage = Color.parseColor("#FBBF24");  // ذهبي
    private final int colorDoc   = Color.parseColor("#FB7185");  // وردي محمر
    private final int colorAudio = Color.parseColor("#34D399");  // أخضر نعناعي
    private final int colorTextSecondary = Color.parseColor("#94A3B8");

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileItem item = fileList.get(position);

        // 1. عرض الاسم المقترح (New Name)
        holder.fileName.setText(item.newName != null ? item.newName : item.oldName);

        // 2. عرض حجم الملف بشكل احترافي (مثلاً: 1.5 MB)
        // دالة Formatter.formatFileSize هي دالة أندرويد رسمية لتحويل الـ Bytes لـ MB/KB
        String sizeStr = Formatter.formatFileSize(holder.itemView.getContext(), item.fileSize);

        // 3. دمج الحجم مع النوع (مثلاً: VIDEO • 14 MB)
        String typeInfo = (item.fileType != null ? item.fileType.toUpperCase() : "FILE") + " • " + sizeStr;
        holder.fileType.setText(typeInfo);

        // 4. منطق الألوان والأيقونات بناءً على النوع
        setupStyleByType(holder, item);

        // 5. حالة التعديل: إذا كان الاسم سيتغير، نضيء المؤشر
        boolean isModified = !item.oldName.equals(item.newName);
        if (holder.statusIcon != null) {
            holder.statusIcon.setVisibility(isModified ? View.VISIBLE : View.GONE);
        }
    }

    private void setupStyleByType(FileViewHolder holder, FileItem item) {
        String ext = (item.fileType != null) ? item.fileType.toLowerCase() : "";
        int themeColor;
        int iconRes;

        if (ext.matches("^(mp4|mkv|mov|avi|webm|3gp)$")) {
            themeColor = colorVideo;
            iconRes = android.R.drawable.ic_media_play;
        } else if (ext.matches("^(jpg|png|jpeg|gif|webp|heic)$")) {
            themeColor = colorImage;
            iconRes = android.R.drawable.ic_menu_gallery;
        } else if (ext.matches("^(pdf|doc|docx|txt|ppt|pptx|xls|xlsx)$")) {
            themeColor = colorDoc;
            iconRes = android.R.drawable.ic_menu_edit;
        } else if (ext.matches("^(mp3|wav|m4a|ogg|aac|opus)$")) {
            themeColor = colorAudio;
            iconRes = android.R.drawable.ic_lock_silent_mode_off;
        } else {
            themeColor = Color.WHITE;
            iconRes = android.R.drawable.ic_menu_agenda;
        }

        holder.fileIcon.setImageResource(iconRes);
        holder.fileIcon.setColorFilter(themeColor);
        holder.fileType.setTextColor(themeColor);
        // جعل اسم الملف أبيض دائماً للوضوح، ولكن يتغير للون الثيم عند التعديل
        holder.fileName.setTextColor(item.oldName.equals(item.newName) ? Color.WHITE : themeColor);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    /**
     * تحديث القائمة باستخدام DiffUtil (المحرك الأسطوري للسرعة)
     */
    public void updateList(List<FileItem> newList) {
        if (newList == null) return;

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return fileList.size(); }
            @Override
            public int getNewListSize() { return newList.size(); }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return fileList.get(oldPos).fileUri.equals(newList.get(newPos).fileUri);
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                FileItem oldItem = fileList.get(oldPos);
                FileItem newItem = newList.get(newPos);
                return oldItem.newName.equals(newItem.newName) &&
                        oldItem.oldName.equals(newItem.oldName);
            }
        });

        this.fileList.clear();
        this.fileList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    // متوافق مع كود MainActivity القديم إذا كنت تستخدم setFiles
    public void setFiles(List<FileItem> newList) {
        updateList(newList);
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileType;
        ImageView fileIcon, statusIcon;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileType = itemView.findViewById(R.id.file_type);
            fileIcon = itemView.findViewById(R.id.img_file_type);
            statusIcon = itemView.findViewById(R.id.status_indicator);
        }
    }
}