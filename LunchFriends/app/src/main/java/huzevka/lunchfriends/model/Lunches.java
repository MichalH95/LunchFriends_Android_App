package huzevka.lunchfriends.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="lunches")
public class Lunches {

	@ElementList(name="lunch", inline=true, required = false)
	private List<Lunch> lunch;

	public List<Lunch> getLunch() {
		return lunch;
	}

	public void setLunch(List<Lunch> lunch) {
		this.lunch = lunch;
	}
}
