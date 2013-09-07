/* ROBOTER - reduced front-end for javakarol.Roboter (as in the javakarol BlueJ project)
 * 
 * Copyright (c) 2013, Johannes Rosenberger <jo.rosenberger(at)gmx-topmail.de>
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

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
