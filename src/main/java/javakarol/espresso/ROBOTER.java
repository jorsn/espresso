package javakarol.espresso;

import javakarol.Roboter;
import javakarol.Welt;

public class ROBOTER extends Roboter {

	public ROBOTER(Welt inWelt) {
		super(inWelt);
		// TODO Auto-generated constructor stub
	}

	public ROBOTER(int startX, int startY, char startBlickrichtung, Welt inWelt) {
		super(startX, startY, startBlickrichtung, inWelt);
		// TODO Auto-generated constructor stub
	}

	public void Schritt() {
		super.Schritt();
	}
	
	public void LinksDrehen() {
		super.LinksDrehen();
	}
	
	public void RechtsDrehen()
	{
		super.RechtsDrehen();
	}
	
	public void Hinlegen()
	{
		super.Hinlegen();
	}
	
	public void Aufheben()
	{
		super.Aufheben();
	}
	
	public void MarkeSetzen()
	{
		super.MarkeSetzen();
	}
	
	public void MarkeLoeschen()
	{
		super.MarkeLoeschen();
	}
	
	public void TonErzeugen()
	{
		super.TonErzeugen();
	}
	
	public boolean IstWand()
	{
		return super.IstWand();
	}
	
	public boolean IstZiegel()
	{
		return super.IstZiegel();
	}
	
	public boolean IstMarke()
	{
		return super.IstMarke();
	}
}
