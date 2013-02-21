package com.example.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.example.queryscore.R;
public class SAXCourseService {
	private CourseParser courseParser 	  = null;
	public static final String YearTermNO = "term";
	public static final String VALUE	  = "value";
	
	public SAXCourseService(File file) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			parser = factory.newSAXParser();
			courseParser = new CourseParser();
			parser.parse(file, courseParser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Course> getScores(){
		return courseParser.courses;
	}
	
	public List<HashMap<String, String>> getYearTermNO(){
		return courseParser.maps;
	}
	
	public String getTitle(Context context){
		String title = courseParser.title;
		if (title != null && title.length() != 0){
			String[] tmp = title.split("学号:")[1].split("姓名:");
			return context.getString(R.string.student_id) + tmp[0] +
					context.getString(R.string.blank) + 
					context.getString(R.string.student_name) + tmp[1];
		}
		return null;
	}
	
	public String getTotalNO(Context context){
		String totalNO = courseParser.totalNO;
		if (totalNO != null && totalNO.length() != 0){
			String tmp[] = totalNO.split("共有记录数");
			return context.getString(R.string.totalNO) + tmp[1];
		}
		return null;
	}
	
	private final class CourseParser extends DefaultHandler{
		public List<Course> courses			  = new ArrayList<Course>();
		public List<HashMap<String, String>> maps = new ArrayList<HashMap<String,String>>();
		private boolean isFinish 			  = true;
		private boolean isTitle				  = false;
		private boolean isTotalNO			  = false;
		private Course course 				  = null;
		private int flag 					  = 0;
		private HashMap<String, String> options   = null;
		private String value 				  = null;
		private String tag 					  = null;
		public String title				  	  = null;
		public String totalNO				  = null;
		
		
		@Override
		public void startDocument() throws SAXException {
			courses = new ArrayList<Course>();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("option".equals(localName) && attributes != null){
				value = attributes.getValue(0);
			}
			else if( attributes != null && attributes.getValue(0) != null 
					&& (attributes.getValue(0).equals("color-rowNext") || 
							attributes.getValue(0).equals("color-row")) && 
							"tr".equals(localName)){
				course = new Course();
				flag = 0;
				isFinish = false;
			}
			else if (attributes != null && attributes.getValue(0) != null &&
					attributes.getValue(0).equals("36") && "td".equals(localName)){
				isTitle = true;
			}
			else if (attributes != null && attributes.getValue(0) != null &&
					attributes.getValue(0).equals("9") && "td".equals(localName)){
				isTotalNO = true;
			}
			tag = localName;
		}
		
		@Override
		public void characters(char[] ch, int start, int length)throws SAXException {
			if ("option".equals(tag)){
				options = new HashMap<String, String>();
				options.put(YearTermNO, new String(ch, start,length).trim());
				options.put(VALUE, value);
			}
			else if	(isTitle){
				title = new String(ch, start,length).trim();
				isTitle = false;
			}
			else if(isTotalNO){
				totalNO = new String(ch, start,length).trim();
				isTotalNO = false;
			}
			else if(!isFinish && "td".equals(tag) ){ // && length != 0
				flag++;
				String useful = new String(ch, start,length).trim();
				useful = useful.length() == 0 ? "" : useful ;
				switch (flag) {
				case 1:
					course.setProperity(useful);
					break;
				case 2:
					course.setId(useful);
					break;
				case 3:
					course.setName(useful);
					if(useful.equals(" 毛泽东思想和中国特色社会主义理论体系概论")){
						course.setTestType("未知");
						flag++;
					}
					break;
				case 4:
					course.setTestType(useful);
					break;
				case 5:
					course.setLength(useful);
					break;
				case 6:
					course.setScoreType(useful);
					break;
				case 7:
					course.setScore(useful);
					isFinish = true;
					break;
				default:
					break;
				}
				
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(isFinish && course != null && "td".equals(tag)){
				courses.add(course);
				course = null;
			}
			else if(options != null && "option".equals(tag)){
				maps.add(options);
				options = null;
			}
			tag = null;
		}
		
	}
}
