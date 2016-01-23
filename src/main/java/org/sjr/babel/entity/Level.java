package org.sjr.babel.entity;

public class Level {

	private Integer id;
	private String name, description;
	private Level next, previous;

	
	public Level (Integer id, String name, String description , Level next , Level previous){
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.next= next;
		this.previous = previous;
	}; 
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Level getNext() {
		return next;
	}

	public void setNext(Level next) {
		this.next = next;
	}

	public Level getPrevious() {
		return previous;
	}

	public void setPrevious(Level previous) {
		this.previous = previous;
	}

}
