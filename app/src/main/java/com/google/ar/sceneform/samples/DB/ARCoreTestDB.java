package com.google.ar.sceneform.samples.DB;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {CourseInfo.class, TrackedImgInfo.class}, version = 2)
public abstract class ARCoreTestDB extends RoomDatabase {
	private static ARCoreTestDB INSTANCE;
	private static final String DB_NAME ="ARCORETEST.db";

	public static ARCoreTestDB getDatabase(final Context context) {
		if(INSTANCE == null) {
			synchronized (ARCoreTestDB.class) {
				if(INSTANCE == null) {
					INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ARCoreTestDB.class, DB_NAME)
							.allowMainThreadQueries()
//							.addMigrations(MIGRATION_1_2)
							.build();
				}
			}
		}
		return INSTANCE;
	}

	public abstract CourseDao courseDao();
	public abstract TrackedImgDao trackedImgDao();

	static final Migration MIGRATION_1_2 = new Migration(3, 4) {
		@Override
		public void migrate(@NonNull SupportSQLiteDatabase database) {
			//database.execSQL("ALTER TABLE courseinfo ADD COLUMN name TEXT NOT NULL DEFAULT 'Non Title'");
		}
	};
}