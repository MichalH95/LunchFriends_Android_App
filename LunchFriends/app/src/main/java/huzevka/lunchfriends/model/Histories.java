package huzevka.lunchfriends.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="histories")
public class Histories {

	@ElementList(name="history", inline=true, required = false)
	private List<History> history;

	public List<History> getHistory() {
		return history;
	}

	public void setHistory(List<History> history) {
		this.history = history;
	}

}
