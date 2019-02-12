package huzevka.lunchfriends.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name="person")
public class Person implements Serializable {

	static final long serialVersionUID = 7872965130203967083L;

	@Element(name = "PID", required = false)
	int PID;
	@Element(name = "realname", required = false)
	String realname;
	@Element(name = "gender", required = false)
	String gender;
	@Element(name = "age", required = false)
	int age;
	@Element(name = "userid", required = false)
	String userid;
	@Element(name = "passwd", required = false)
	String passwd;
	@Element(name = "imageuri", required = false)
	String imageuri;
	@Element(name = "email", required = false)
	String email;
	@Element(name = "phone", required = false)
	String phone;
	@Element(name = "hobbies", required = false)
	String hobbies;

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

	@Override
	public String toString() {
		return "Person{" +
				"PID=" + PID +
				", realname='" + realname + '\'' +
				", gender='" + gender + '\'' +
				", age=" + age +
				", userid='" + userid + '\'' +
				", passwd='" + passwd + '\'' +
				", imageuri='" + imageuri + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", hobbies='" + hobbies + '\'' +
				'}';
	}
}

