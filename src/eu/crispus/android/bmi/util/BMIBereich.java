package eu.crispus.android.bmi.util;

/**
 * Klasse um unterschiedliche BMI-Bereiche zu entsprenden Altern speichern zu können.
 * BMI-Bereich 1: Sie sind untergewichtig
 * BMI-Bereich 2: Sie haben Ihr Normalgewicht
 * BMI-Bereich 3: Sie sind etwas übergewichtig
 * BMI-Bereich 4: Sie sind übergewichtig
 * BMI-Bereich 5: Sie haben erhebliches Übergewicht
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class BMIBereich {
	/**
	 * Bereich für das Alter.
	 */
	private Bereich alter;
	
	/**
	 * BMI-Bereich für untergewicht.
	 */
	private Bereich bereich1;
	
	/**
	 * BMI-Bereich für Normalgewicht.
	 */
	private Bereich bereich2;
	
	/**
	 * BMI-Bereich für etwas übergewichtig.
	 */
	private Bereich bereich3;

	/**
	 * BMI-Bereich für übergewichtig.
	 */
	private Bereich bereich4;
	
	/**
	 * BMI-Bereich für erhebliches Übergewicht.
	 */
	private Bereich bereich5;

	/**
	 * Konstruktor um alle Felder vor zu belegen.
	 * 
	 * @param alter Bereich für das Alter.
	 * @param bereich1 BMI-Bereich für untergewicht.
	 * @param bereich2 BMI-Bereich für Normalgewicht.
	 * @param bereich3 BMI-Bereich für etwas übergewichtig.
	 * @param bereich4 BMI-Bereich für übergewichtig.
	 * @param bereich5 BMI-Bereich für erhebliches Übergewicht.
	 */
	public BMIBereich(Bereich alter, Bereich bereich1, Bereich bereich2, Bereich bereich3, Bereich bereich4, Bereich bereich5) {
		super();
		this.alter = alter;
		this.bereich1 = bereich1;
		this.bereich2 = bereich2;
		this.bereich3 = bereich3;
		this.bereich4 = bereich4;
		this.bereich5 = bereich5;
	}

	public Bereich getAlter() {
		return alter;
	}

	public void setAlter(Bereich alter) {
		this.alter = alter;
	}

	public Bereich getBereich1() {
		return bereich1;
	}

	public void setBereich1(Bereich bereich1) {
		this.bereich1 = bereich1;
	}

	public Bereich getBereich2() {
		return bereich2;
	}

	public void setBereich2(Bereich bereich2) {
		this.bereich2 = bereich2;
	}

	public Bereich getBereich3() {
		return bereich3;
	}

	public void setBereich3(Bereich bereich3) {
		this.bereich3 = bereich3;
	}

	public Bereich getBereich4() {
		return bereich4;
	}

	public void setBereich4(Bereich bereich4) {
		this.bereich4 = bereich4;
	}

	public Bereich getBereich5() {
		return bereich5;
	}

	public void setBereich5(Bereich bereich5) {
		this.bereich5 = bereich5;
	}
}
