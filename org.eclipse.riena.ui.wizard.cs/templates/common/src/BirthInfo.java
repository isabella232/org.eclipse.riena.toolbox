package ${package};

import java.util.Date;

public class BirthInfo {
	private Date birthDay;
	private String birthPlace;

	public BirthInfo() {

	}

	public BirthInfo(Date day, String place) {
		this.birthDay = day;
		this.birthPlace = place;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date day) {
		this.birthDay = day;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
}
