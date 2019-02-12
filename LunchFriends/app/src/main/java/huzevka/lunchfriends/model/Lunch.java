package huzevka.lunchfriends.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.sql.Date;

@Root(name="lunch")
public class Lunch implements Serializable {

	static final long serialVersionUID = -1806398929436683952L;

	@Element(name = "LID")
	int LID;
	@Element(name = "placeid")
	String placeid;
	@Element(name = "ldate")
	String ldate;
	@Element(name = "PID")
	int PID;

	public int getLID() {
		return LID;
	}

	public void setLID(int LID) {
		this.LID = LID;
	}

	public String getPlaceid() {
		return placeid;
	}

	public void setPlaceid(String placeid) {
		this.placeid = placeid;
	}

	public String getLdate() {
		return ldate;
	}

	public void setLdate(String ldate) {
		this.ldate = ldate;
	}

	public int getPID() {
		return PID;
	}

	public void setPID(int PID) {
		this.PID = PID;
	}

	@Override
	public String toString() {
		return "Lunch{" +
				"LID=" + LID +
				", placeid='" + placeid + '\'' +
				", ldate='" + ldate + '\'' +
				", PID=" + PID +
				'}';
	}
}

