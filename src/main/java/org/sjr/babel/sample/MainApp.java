package org.sjr.babel.sample;

import java.util.ArrayList;
import java.util.List;

public class MainApp {

	public static List<Person> personList(){
		List<Person> lst = new ArrayList<>();
		lst.add(new EnglishPerson());
		lst.add(new SpanishPerson());
		lst.add(new FrenchPerson());

		return lst;
	}
	
	public static void main(String[] args) {
		List<Person> persons = personList();
		for (Person person : persons) {
			System.out.println(person.sayHello());
		}

	}
}
