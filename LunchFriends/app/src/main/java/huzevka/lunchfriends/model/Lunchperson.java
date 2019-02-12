package huzevka.lunchfriends.model;

import android.support.annotation.NonNull;
import android.util.Log;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import huzevka.lunchfriends.R;

@Root(name="lunchperson")
public class Lunchperson implements Comparable {

	private final String TAG = "Lunchperson";

	static final long serialVersionUID = 347295308394211L;

	@Element(name = "PID")
	int PID;
	@Element(name = "realname")
	String realname;
	@Element(name = "gender")
	String gender;
	@Element(name = "age")
	int age;
	@Element(name = "userid")
	String userid;
	@Element(name = "passwd")
	String passwd;
	@Element(name = "imageuri")
	String imageuri;
	@Element(name = "email")
	String email;
	@Element(name = "phone")
	String phone;
	@Element(name = "hobbies")
	String hobbies;
	@Element(name = "LID")
	int LID;
	@Element(name = "ldate")
	String ldate;
	@Element(name = "placeid")
	String placeid;

	public int getPID() {
		return PID;
	}

	public void setPID(int PID) {
		this.PID = PID;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getImageuri() {
		return imageuri;
	}

	public void setImageuri(String imageuri) {
		this.imageuri = imageuri;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHobbies() {
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	public int getLID() {
		return LID;
	}

	public void setLID(int LID) {
		this.LID = LID;
	}

	public String getLdate() {
		return ldate;
	}

	public void setLdate(String ldate) {
		this.ldate = ldate;
	}

	public String getPlaceid() {
		return placeid;
	}

	public void setPlaceid(String placeid) {
		this.placeid = placeid;
	}

	@Override
	public int compareTo(@NonNull Object o) {
		if ( ! (o instanceof Lunchperson) ) {
			Log.e(TAG, "Error comparing");
			return 0;
		} else {
			Lunchperson p = (Lunchperson) o;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			java.util.Date date1;
			java.util.Date date2;
			try {
				date1 = format.parse(getLdate());
			} catch (ParseException e) {
				try {
					format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					date1 = format.parse(getLdate());
				} catch (ParseException ex) {
					Log.e(TAG, "Error comparing");
					return 0;
				}
			}
			format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			try {
				date2 = format.parse(p.getLdate());
			} catch (ParseException e) {
				try {
					format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					date2 = format.parse(p.getLdate());
				} catch (ParseException ex) {
					Log.e(TAG, "Error comparing");
					return 0;
				}
			}
			return date1.compareTo(date2);
		}
	}
}

