package com.example.reorderpro;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * 🚀 ReOrder Pro - The Legendary Database Engine
 * تم رفع الإصدار إلى 3 لحل مشكلة Schema Mismatch وتفعيل الترحيل التلقائي.
 */
@Database(entities = {FileItem.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract FileDao fileDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "reorder_pro_secure_db")
                            /* 🔥 هذه الدالة حاسمة: عند تغيير رقم الـ Version من 2 إلى 3،
                               سيقوم Room بمسح قاعدة البيانات القديمة وإنشائها من جديد
                               بالهيكل الصحيح، وهذا سيحل مشكلة الـ Crash فوراً.
                            */
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}