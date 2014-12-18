package org.keyser.anr.web.dto;

public class ServerDto {

	public enum Operation {
		create, delete
	}

	private int id;

	private String name;

	private Operation operation;

}
