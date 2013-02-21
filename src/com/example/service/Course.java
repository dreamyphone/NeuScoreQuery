package com.example.service;

public class Course {
	private String properity;
	private String id;
	private String name;
	private String testType;
	private String length;
	private String scoreType;
	private String score;
	
	public String getProperity() {
		return properity;
	}
	public void setProperity(String properity) {
		this.properity = properity;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getScoreType() {
		return scoreType;
	}
	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	@Override
	public String toString() {
		return "Course [properity=" + properity + ", id=" + id + ", name="
				+ name + ", testType=" + testType + ", length=" + length
				+ ", scoreType=" + scoreType + ", score=" + score + "]";
	}

}
