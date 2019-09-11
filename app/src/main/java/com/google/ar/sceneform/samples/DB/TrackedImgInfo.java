package com.google.ar.sceneform.samples.DB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "trackedimginfo", foreignKeys = @ForeignKey(entity = CourseInfo.class, parentColumns = "id", childColumns = "course_id", onUpdate = CASCADE), indices = @Index("course_id"))
public class TrackedImgInfo {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private int id;

	@ColumnInfo(name = "course_id")
	private int courseID;

	@ColumnInfo(name = "imgname")
	private String imgName;

	@ColumnInfo(name = "directoryimg")
	private String directoryImg;

	public TrackedImgInfo(int courseID, String imgName, String directoryImg) {
		this.courseID = courseID;
		this.imgName = imgName;
		this.directoryImg = directoryImg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getDirectoryImg() {
		return directoryImg;
	}

	public void setDirectoryImg(String directoryImg) {
		this.directoryImg = directoryImg;
	}
}
