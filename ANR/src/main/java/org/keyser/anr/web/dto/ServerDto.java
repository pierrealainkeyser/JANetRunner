package org.keyser.anr.web.dto;

public class ServerDto {


	public enum Operation {
		create, delete
	}

	private int id;

	private String name;

	private Operation operation;

	public ServerDto() {
		super();
	}

	public ServerDto(int id, String name, Operation operation) {
		super();
		this.id = id;
		this.name = name;
		this.operation = operation;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

}
