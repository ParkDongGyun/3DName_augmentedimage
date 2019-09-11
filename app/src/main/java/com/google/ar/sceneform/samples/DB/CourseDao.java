package com.google.ar.sceneform.samples.DB;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CourseDao {

	@Query("Select * From courseInfo")
	LiveData<List<CourseInfo>> getAll();

	@Query("Select Count (*) From courseInfo")
	int getAllCount();

	@Query("Select * From courseInfo Where id ==:uid")
	CourseInfo getCourseInfo(int uid);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long insert(CourseInfo courseInfo);

	@Query("Select * From courseInfo Where tracckedimgcount == :count")
	List<CourseInfo> getAllTrackedCount(int count);
	@Update
	void update(CourseInfo courseInfo);

	@Delete
	void delete(CourseInfo courseInfo);
}
