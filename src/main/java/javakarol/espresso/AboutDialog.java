/* About - The about windows for Espresso Karol
 * 
 * Copyright (C) 2011, Johannes Rosenberger <jo.rosenberger@gmx-topmail.de>
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

/**
 * 
 */
package javakarol.espresso;

import java.io.File;
import java.io.IOException;

import java.awt.Dimension;
import java.awt.Desktop;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JFrame;

class AboutDialog {
	private Settings settings;

	private String espressoName;
	private String version;

	private String iconURLString;


	private JFrame aboutDialog;
	private JTextPane aboutPane;

	private String aboutText;

	protected AboutDialog (Settings settings) {

		this.settings = settings;

		this.espressoName = Settings.NAME;
		this.version = Settings.VERSION;

		iconURLString = new File(settings.getIconPath()).toURI().toString();

		aboutDialog = new JFrame("Über " + espressoName);

		aboutPane = new JTextPane();
		aboutPane.setEditable(false);
		aboutPane.setContentType("text/html");

		initGUI();
	}

	void initGUI() {
		String githubURL = "https://github.com/jorsn/espresso";
		boolean desktopSupport = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);

		aboutText = "<html>"
			+ "<head>"
			+ "<title>" + aboutDialog.getTitle() + "</title>"
			+ "</head>"
			+ "<body width=\"600px\">"
			+ "<center><img src=\"" + iconURLString + "\"></img></center>"
			+ "<p><b>"
			+ espressoName + ", version " + version
			+ ", Copyright (C) 2013, Johannes Rosenberger"
			+ "</b></p>"
			+ "<p align=\"justify\">"
			+ "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND "
			+ "ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED "
			+ "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. "
			+ "IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, "
			+ "INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT "
			+ "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, "
			+ "OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, "
			+ "WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) "
			+ "ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY "
			+ "OF SUCH DAMAGE."
			+ "</p>"
			+ "<p>"
			+ "For more details please read the 'README.md' and the 'LICENSE' file, which can be found "
			+ "in <code>" + settings.getAppHome() + "</code> or on "
			+ (desktopSupport ? "<a href=\"" + githubURL + "\" title=\"GitHub: Espresso Karol\">"
					+ "the project site on GitHub</a>."
					: "'" + githubURL + "'.")
			+ "</p>"
			+ "</body>"
			+ "</html>";
		aboutPane.setText(aboutText);
		if (desktopSupport) {
			aboutPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType().equals(EventType.ACTIVATED)) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(aboutDialog, "Fehler: Der Standardbrowser ist "
								+ "nicht gesetzt oder lässt sich nicht ausführen.", "FEHLER",
								JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(aboutDialog, "Fehler: " + ex.getMessage()
								, "FEHLER",
								JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
						}
					}
				}
			});
		}
		aboutDialog.add(aboutPane);
		aboutDialog.pack();
		aboutDialog.setVisible(true);
	}
}

// vim: foldmethod=syntax
