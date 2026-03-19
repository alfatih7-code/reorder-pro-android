package com.example.reorderpro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.reorderpro.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppDatabase db;
    private FileAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final int PICK_FOLDER_REQUEST = 100;
    private Uri currentTreeUri;
    private boolean isOldestFirst = true;

    private enum AppState { IDLE, READY_TO_APPLY, DONE_CAN_UNDO }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);

        setupRecyclerView();
        initListeners();
        observeDatabase();
        restoreLastSession();
    }

    private void setupRecyclerView() {
        adapter = new FileAdapter();
        binding.rvFiles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFiles.setHasFixedSize(true);
        binding.rvFiles.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnSortOrder.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            isOldestFirst = !isOldestFirst;
            v.animate().rotationBy(180f).setDuration(300).start();
            if (currentTreeUri != null) scanAndSyncFolder(currentTreeUri);
        });

        binding.namingOptionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            binding.customNameLayout.setVisibility(checkedId == R.id.radio_custom ? View.VISIBLE : View.GONE);
            updatePreviewNames();
        });

        binding.etCustomName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreviewNames(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.btnPick.setOnClickListener(v -> openDirectoryPicker());
        binding.btnApply.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            startRenamingProcess();
        });
        binding.btnUndo.setOnClickListener(v -> performUndo());
        binding.btnNewFolder.setOnClickListener(v -> openDirectoryPicker());
    }

    private void observeDatabase() {
        db.fileDao().getAllFiles().observe(this, files -> {
            if (files != null) {
                adapter.updateList(files);
                updateUIStateBasedOnData(files);
            }
        });
    }

    private void updateUIStateBasedOnData(List<FileItem> files) {
        if (files.isEmpty()) {
            setAppState(AppState.IDLE);
            return;
        }

        executorService.execute(() -> {
            int renamedCount = db.fileDao().getRenamedFilesCount();
            runOnUiThread(() -> {
                if (renamedCount > 0) {
                    setAppState(AppState.DONE_CAN_UNDO);
                } else {
                    setAppState(AppState.READY_TO_APPLY);
                }
            });
        });
    }

    private void setAppState(AppState state) {
        // Reset Visibilities
        binding.btnPick.setVisibility(View.GONE);
        binding.btnApply.setVisibility(View.GONE);
        binding.btnUndo.setVisibility(View.GONE);
        binding.btnNewFolder.setVisibility(View.GONE);
        binding.optionsCard.setVisibility(View.GONE);

        // تخصيص الأوزان (Weights) برمجياً لتطابق الـ XML الاحترافي
        LinearLayout.LayoutParams pickParams = (LinearLayout.LayoutParams) binding.btnPick.getLayoutParams();
        LinearLayout.LayoutParams applyParams = (LinearLayout.LayoutParams) binding.btnApply.getLayoutParams();

        switch (state) {
            case IDLE:
                binding.btnPick.setVisibility(View.VISIBLE);
                binding.btnPick.setText("SELECT FOLDER");
                pickParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                pickParams.weight = 0;
                binding.statusText.setText("Ready to start! Select a folder.");
                break;

            case READY_TO_APPLY:
                binding.optionsCard.setVisibility(View.VISIBLE);
                binding.btnPick.setVisibility(View.VISIBLE);
                binding.btnApply.setVisibility(View.VISIBLE);

                binding.btnPick.setText("CHANGE");
                // تطبيق توزيع 3:4 كما في الـ XML
                pickParams.width = 0;
                pickParams.weight = 3.0f;
                applyParams.width = 0;
                applyParams.weight = 4.0f;

                binding.statusText.setText("Review names then click apply.");
                break;

            case DONE_CAN_UNDO:
                binding.optionsCard.setVisibility(View.VISIBLE);
                binding.btnUndo.setVisibility(View.VISIBLE);
                binding.btnNewFolder.setVisibility(View.VISIBLE);
                binding.statusText.setText("Task completed! You can undo or start new.");
                break;
        }
        binding.btnPick.setLayoutParams(pickParams);
        binding.btnApply.setLayoutParams(applyParams);
    }

    private void openDirectoryPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, PICK_FOLDER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FOLDER_REQUEST && resultCode == RESULT_OK && data != null) {
            currentTreeUri = data.getData();
            if (currentTreeUri != null) {
                saveLastUri(currentTreeUri);
                getContentResolver().takePersistableUriPermission(currentTreeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                scanAndSyncFolder(currentTreeUri);
            }
        }
    }

    private void scanAndSyncFolder(Uri treeUri) {
        runOnUiThread(() -> binding.mainProgress.setVisibility(View.VISIBLE));
        executorService.execute(() -> {
            try {
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                if (pickedDir == null) return;

                DocumentFile[] rawFiles = pickedDir.listFiles();
                List<FileItem> syncList = new ArrayList<>();

                for (DocumentFile f : rawFiles) {
                    if (f.isFile() && f.getName() != null) {
                        FileItem existing = db.fileDao().findByFingerprint(f.lastModified(), f.length());
                        if (existing != null) {
                            existing.fileUri = f.getUri().toString();
                            existing.oldName = f.getName();
                            syncList.add(existing);
                        } else {
                            syncList.add(new FileItem(
                                    f.getUri().toString(), f.getName(), f.getName(),
                                    f.getName(), getFileExtension(f.getName()),
                                    f.lastModified(), f.length()
                            ));
                        }
                    }
                }

                Collections.sort(syncList, (a, b) -> {
                    int res = Long.compare(a.lastModified, b.lastModified);
                    return isOldestFirst ? res : -res;
                });

                db.fileDao().deleteAllFiles();
                db.fileDao().insertAll(syncList);
                updatePreviewNames();

                runOnUiThread(() -> binding.mainProgress.setVisibility(View.GONE));
            } catch (Exception e) {
                Log.e("PRO_LOG", "Scan Error", e);
                runOnUiThread(() -> binding.mainProgress.setVisibility(View.GONE));
            }
        });
    }

    private void updatePreviewNames() {
        executorService.execute(() -> {
            List<FileItem> currentItems = db.fileDao().getAllFilesList();
            int selectedId = binding.namingOptionsGroup.getCheckedRadioButtonId();
            String prefix = binding.etCustomName.getText().toString().trim();

            for (int i = 0; i < currentItems.size(); i++) {
                FileItem item = currentItems.get(i);
                String generatedName = NamingHelper.generateName(
                        item.originalName, i + 1, currentItems.size(),
                        selectedId, item.fileType, prefix
                );
                db.fileDao().updateNewNameOnly(item.fileUri, generatedName);
            }
        });
    }

    private void startRenamingProcess() {
        runOnUiThread(() -> binding.mainProgress.setVisibility(View.VISIBLE));
        executorService.execute(() -> {
            try {
                List<FileItem> items = db.fileDao().getAllFilesList();
                DocumentFile parentDir = DocumentFile.fromTreeUri(this, currentTreeUri);
                if (parentDir == null) return;

                for (FileItem item : items) {
                    if (item.oldName.equals(item.newName)) continue;
                    DocumentFile file = parentDir.findFile(item.oldName);
                    if (file != null && file.exists()) {
                        if (file.renameTo(item.newName)) {
                            db.fileDao().updateAfterRename(item.fileUri, file.getUri().toString(), item.newName);
                        }
                    }
                }
                runOnUiThread(() -> {
                    binding.mainProgress.setVisibility(View.GONE);
                    Toast.makeText(this, "Masterfully Renamed! 🚀", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> binding.mainProgress.setVisibility(View.GONE));
            }
        });
    }

    private void performUndo() {
        runOnUiThread(() -> binding.mainProgress.setVisibility(View.VISIBLE));
        executorService.execute(() -> {
            try {
                List<FileItem> items = db.fileDao().getAllFilesList();
                DocumentFile parentDir = DocumentFile.fromTreeUri(this, currentTreeUri);
                if (parentDir == null) return;

                for (FileItem item : items) {
                    if (item.oldName.equals(item.originalName)) continue;
                    DocumentFile file = parentDir.findFile(item.oldName);
                    if (file != null && file.exists()) {
                        if (file.renameTo(item.originalName)) {
                            db.fileDao().updateAfterRename(item.fileUri, file.getUri().toString(), item.originalName);
                        }
                    }
                }
                scanAndSyncFolder(currentTreeUri);
                runOnUiThread(() -> Toast.makeText(this, "Success Reverted! ⏪", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> binding.mainProgress.setVisibility(View.GONE));
            }
        });
    }

    private void saveLastUri(Uri uri) {
        getSharedPreferences("pro_prefs", MODE_PRIVATE).edit().putString("last_uri", uri.toString()).apply();
    }

    private void restoreLastSession() {
        String uriStr = getSharedPreferences("pro_prefs", MODE_PRIVATE).getString("last_uri", null);
        if (uriStr != null) {
            currentTreeUri = Uri.parse(uriStr);
            scanAndSyncFolder(currentTreeUri);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDot = fileName.lastIndexOf(".");
        return (lastDot == -1) ? "" : fileName.substring(lastDot + 1);
    }
}