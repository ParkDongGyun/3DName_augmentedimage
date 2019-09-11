package com.google.ar.sceneform.samples.DB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TrackedImgDao {

	@Query("SELECT * FROM trackedimginfo")
	List<TrackedImgInfo> getAll();

	@Query("SELECT Count (*) FROM trackedimginfo")
	int getAllCount();

	@Query("SELECT * FROM trackedimginfo WHERE course_id == :uid")
	List<TrackedImgInfo> getAllCourse(int uid);

	@Query("SELECT Count (*) FROM trackedimginfo WHERE course_id == :uid")
	int getAllCourseCount(int uid);

	@Query("SELECT * FROM trackedimginfo WHERE imgname == :imgname AND course_id == :courseid")
	TrackedImgInfo findTrackedImg(String imgname, int courseid);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(TrackedImgInfo trackedImgInfo);

	@Update
	void update(TrackedImgInfo trackedImgInfo);
}
