package org.keyser.anr.web.dto;

public final class RoutineDto {

	private final int id;

	private final String text;

	private final Boolean broken;

	public RoutineDto(int id, String text, Boolean broken) {
		this.id = id;
		this.text = text;
		this.broken = broken;
	}

	public String getText() {
		return text;
	}

	public Boolean getBroken() {
		return broken;
	}

	public int getId() {
		return id;
	}

}