package huzevka.lunchfriends.model.distance_matrix;

import org.simpleframework.xml.Root;

@Root(name="DistanceMatrixResponse")
public class DistanceMatrixResponse {

	@org.simpleframework.xml.Element(name = "status")
	String status;

	@org.simpleframework.xml.Element(name = "origin_address")
	String origin_address;

	@org.simpleframework.xml.Element(name = "destination_address")
	String destination_address;

	@org.simpleframework.xml.Element(name = "row")
	Row row;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrigin_address() {
		return origin_address;
	}

	public void setOrigin_address(String origin_address) {
		this.origin_address = origin_address;
	}

	public String getDestination_address() {
		return destination_address;
	}

	public void setDestination_address(String destination_address) {
		this.destination_address = destination_address;
	}

	public Row getRow() {
		return row;
	}

	public void setRow(Row row) {
		this.row = row;
	}
}
