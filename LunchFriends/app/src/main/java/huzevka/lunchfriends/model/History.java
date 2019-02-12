package huzevka.lunchfriends.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="history")
public class History {

	static final long serialVersionUID = 543206732582342L;

	@Element(name = "targetPID")
	int targetPID;
	@Element(name = "PID")
	int PID;
	@Element(name = "placeid")
	String placeid;
	@Element(name = "ldate")
	String ldate;

	public int getTargetPID() {
		return targetPID;
	}

	public void setTargetPID(int targetPID) {
		this.targetPID = targetPID;
	}

	public int getPID() {
		return PID;
	}

	public void setPID(int PID) {
		this.PID = PID;
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
}
