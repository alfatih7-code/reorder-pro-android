package com.example.reorderpro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.example.reorderpro.model.FileItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    private TextView resultText, statusText;
    private ProgressBar progressBar;
    private Button btnRename;

    private View emptyState, card;

    private final List<FileItem> fileList = new ArrayList<>();
    private final HashMap<String, String> renameMap = new HashMap<>();

    private boolean isRenamed = false;
    private String currentFolderUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== INIT VIEWS =====
        resultText = findViewById(R.id.result_text);
        statusText = findViewById(R.id.status_text);
        progressBar = findViewById(R.id.progress_bar);
        btnRename = findViewById(R.id.btn_rename);

        emptyState = findViewById(R.id.empty_state);
        card = findViewById(R.id.card);

        Button btnPick = findViewById(R.id.btn_pick);

        // ===== EVENTS =====
        btnPick.setOnClickListener(v -> openFolder());

        btnRename.setOnClickListener(v -> {
            if (isRenamed) {
                undoRename();
            } else {
                applyRename();
            }
        });

        showEmptyState();
    }

    // ================= UI =================

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        card.setVisibility(View.GONE);
        btnRename.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
    }

    private void showContent() {
        emptyState.setVisibility(View.GONE);
        card.setVisibility(View.VISIBLE);
        btnRename.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.VISIBLE);
    }

    // ================= PICK FOLDER =================

    private void openFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();
            if (uri == null) return;

            currentFolderUri = uri.toString();

            getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            readFiles(uri);
        }
    }

    // ================= READ FILES =================

    private void readFiles(Uri uri) {

        DocumentFile folder = DocumentFile.fromTreeUri(this, uri);
        if (folder == null) return;

        fileList.clear();

        int images = 0, videos = 0;

        for (DocumentFile file : folder.listFiles()) {

            if (file != null && file.isFile()) {

                FileItem item = new FileItem(
                        file.getName(),
                        file.lastModified(),
                        file.getType(),
                        file
                );

                fileList.add(item);

                if (item.isImage()) images++;
                if (item.isVideo()) videos++;
            }
        }

        showContent();

        statusText.setText("Files: " + fileList.size()
                + " | Images: " + images
                + " | Videos: " + videos);

        resultText.setText(buildList());
    }

    private String buildList() {

        if (fileList.isEmpty()) return "No files";

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < fileList.size(); i++) {
            builder.append(i + 1)
                    .append(". ")
                    .append(fileList.get(i).getName())
                    .append("\n");
        }

        return builder.toString();
    }

    // ================= RENAME =================

    private void applyRename() {

        if (fileList.isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);
        statusText.setText("Renaming...");

        new Thread(() -> {

            renameMap.clear();
            int success = 0;

            for (int i = 0; i < fileList.size(); i++) {

                FileItem item = fileList.get(i);
                String newName = generateName(item, i);

                try {
                    boolean ok = item.getFile().renameTo(newName);

                    if (ok) {
                        renameMap.put(newName, item.getName());
                        success++;
                    }

                } catch (Exception ignored) {}
            }

            int finalSuccess = success;

            runOnUiThread(() -> {

                progressBar.setVisibility(View.GONE);
                statusText.setText("Done: " + finalSuccess);

                isRenamed = true;
                updateButton();

                readFiles(Uri.parse(currentFolderUri));
            });

        }).start();
    }

    // ================= UNDO =================

    private void undoRename() {

        if (renameMap.isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);
        statusText.setText("Restoring...");

        new Thread(() -> {

            int success = 0;

            for (FileItem item : fileList) {

                String current = item.getName();

                if (renameMap.containsKey(current)) {

                    String original = renameMap.get(current);

                    try {
                        boolean ok = item.getFile().renameTo(original);

                        if (ok) success++;

                    } catch (Exception ignored) {}
                }
            }

            int finalSuccess = success;

            runOnUiThread(() -> {

                progressBar.setVisibility(View.GONE);
                statusText.setText("Restored: " + finalSuccess);

                isRenamed = false;
                updateButton();

                readFiles(Uri.parse(currentFolderUri));
            });

        }).start();
    }

    // ================= BUTTON =================

    private void updateButton() {

        if (isRenamed) {
            btnRename.setText("Undo Changes");
            btnRename.setBackgroundColor(0xFFE53935);
        } else {
            btnRename.setText("Apply Changes");
            btnRename.setBackgroundColor(0xFF22C55E);
        }
    }

    // ================= NAME GENERATOR =================

    private String generateName(FileItem item, int index) {

        String ext = item.getExtension();

        if (item.isVideo()) {
            return String.format(Locale.getDefault(), "VID_%03d%s", index + 1, ext);
        }

        if (item.isImage()) {
            return String.format(Locale.getDefault(), "IMG_%03d%s", index + 1, ext);
        }

        return String.format(Locale.getDefault(), "FILE_%03d%s", index + 1, ext);
    }
}