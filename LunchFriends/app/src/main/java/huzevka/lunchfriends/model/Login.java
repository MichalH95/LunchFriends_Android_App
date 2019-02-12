package huzevka.lunchfriends.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="login")
public class Login {

	@Element(name = "userid")
	String userid;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
