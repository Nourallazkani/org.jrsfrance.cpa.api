package org.sjr.babel.model.component;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.sjr.babel.model.entity.reference.Language;
import org.sjr.babel.model.entity.reference.Level;

@Embeddable
public class LanguageSkill {

	@ManyToOne
	private Level level;

	@ManyToOne
	private Language language;

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

}
