package huzevka.lunchfriends.model.distance_matrix;

import org.simpleframework.xml.Root;

@Root(name="element")
public class Element {

	@org.simpleframework.xml.Element(name = "status")
	String status;

	@org.simpleframework.xml.Element(name = "duration")
	Duration duration;

	@org.simpleframework.xml.Element(name = "distance")
	Distance distance;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}
}
