package com.google.ar.sceneform.samples.DB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "courseInfo", indices = @Index(value = "id"))
public class CourseInfo {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private int id;

	@ColumnInfo(name = "name")
	private String name;

	@ColumnInfo(name = "starttime")
	private long startTime;

	@ColumnInfo(name = "endtime")
	private long endTime;

	@ColumnInfo(name = "totalimgcount")
	private int totalImgCount;

	@ColumnInfo(name = "tracckedimgcount")
	private int trackedImgCount;

	public CourseInfo(String name, long startTime, int totalImgCount) {
		this.name = name;
		this.startTime = startTime;
		this.endTime=0;
		this.totalImgCount = totalImgCount;
		this.trackedImgCount = 0;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getTotalImgCount() {
		return totalImgCount;
	}

	public int getTrackedImgCount() {
		return trackedImgCount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public void setTotalImgCount(int totalImgCount) {
		this.totalImgCount = totalImgCount;
	}

	public void setTrackedImgCount(int trackedImgCount) {
		this.trackedImgCount = trackedImgCount;
	}
}