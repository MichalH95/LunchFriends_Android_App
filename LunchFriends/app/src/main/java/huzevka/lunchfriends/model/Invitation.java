package huzevka.lunchfriends.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name="invitation")
public class Invitation implements Serializable {

	static final long serialVersionUID = -1344053264600742561L;

	@Element(name = "PID")
	int PID;

	@Element(name = "targetPID")
	int targetPID;

	@Element(name = "LID")
	int LID;

	public int getPID() {
		return PID;
	}

	public void setPID(int PID) {
		this.PID = PID;
	}

	public int getTargetPID() {
		return targetPID;
	}

	public void setTargetPID(int targetPID) {
		this.targetPID = targetPID;
	}

	public int getLID() {
		return LID;
	}

	public void setLID(int LID) {
		this.LID = LID;
	}

	public Invitation clone() {
		Invitation inv = new Invitation();
		inv.setPID(PID);
		inv.setTargetPID(targetPID);
		inv.setLID(LID);
		return inv;
	}

	@Override
	public String toString() {
		return "Invitation{" +
				"PID=" + PID +
				", targetPID=" + targetPID +
				", LID=" + LID +
				'}';
	}
}
