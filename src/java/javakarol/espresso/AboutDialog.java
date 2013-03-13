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


import ro.jo.java.io.CharsetSensitiveFileToStringReader;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

class AboutDialog {
	private Settings settings;

	private String espressoName;
	private String version;

	private String licensePath;
	private String mplPath;
	private String apacheLicensePath;

	private String iconURLString;


	private JFrame aboutDialog;
	private JPanel aboutPanel;
	private JTextPane aboutPane;

	private String aboutText;

	protected AboutDialog (Settings settings) {

		this.settings = settings;

		this.espressoName = Settings.NAME;
		this.version = Settings.VERSION;

		this.licensePath = settings.getLicensePath();
		this.mplPath = settings.getMplPath();
		this.apacheLicensePath = settings.getApacheLicensePath();
		
		for (String s : new String[] { licensePath, mplPath, apacheLicensePath }) {
			s = String.format("%1s %n%2s%n", s, new File(s).getAbsolutePath());
		}

		iconURLString = new File(settings.getIconPath()).toURI().toString();

		aboutDialog = new JFrame("Über " + espressoName);

		aboutPanel = new JPanel(true);

		aboutPane = new JTextPane();
		aboutPane.setEditable(false);
		aboutPane.setContentType("text/html");

		initGUI();
	}

	void initGUI() {
		aboutText = "<html>"
			+ "<head>"
			+ "<title>" + aboutDialog.getTitle() + "</title>"
			+ "</head>"
			+ "<body>"
			+ "<center><img src=\"" + iconURLString + "\"></img></center>"
			+ "<p>"
			+ espressoName + ", version " + version
			+ ", Copyright (C) 2011, Johannes Rosenberger"
			+ "</p>"
			+ "<p>"
			+ espressoName + " comes with ABSOLUTELY NO WARRANTY. This is free software,<br>"
			+ "and you are welcome to redistribute it under certain conditions."
			+ "</p>"
			+ "<p>"
			+ "Click <a href=license>here</a> for details."
			+ "</p>"
			+ "<p>"
			+ "The javakarol-project files (the files that can be found in 'javakarol.zip')<br>"
			+ "are licensed seperately, as well as juniversalchardet and its source code,<br>"
			+ "which is licensed under the <a href=mpl>Mozilla Public License Version 1.1 (MPLv1.1)</a>.<br>"
			+ "For the javakarol-license, please see 'JavaKarolHandbuch.doc' (in 'javakarol.zip').<br>"
			+ "jsyntaxpane is licensed under the <a href=apacheLicense>Apache License Version 2.0</a><br>"
			+ "and the CharsetSensitiveFileToStringReader is licensed as Espresso Karol (BSD-License)."
			+ "</p>"
			+ "</body>"
			+ "</html>";
		aboutPane.setText(aboutText);
		aboutPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType().equals(EventType.ACTIVATED)) {
					if (e.getDescription().equals("license")) {

						final JFrame licenseWindow = new JFrame(
								"License - " + espressoName);
						final JTextPane license = new JTextPane();
						license.setEditable(false);
						try {
							license.setContentType("text/plain");
							File licenseFile = new File(licensePath);
							//license.read(new FileReader(licenseFile), "license");
							license.setText(CharsetSensitiveFileToStringReader.readFile(licenseFile));
						} catch (Exception e1) {
							e1.printStackTrace();
							license.setContentType("text/html");
							String notFound = "<html>"
								+ "<head>"
								+ "<title>BSD 3 clause license not found</title>"
								+ "</head>"
								+ "<body>"
								+ "<p>"
								+ "FATAL ERROR: The license file could not be found!"
								+ "</p>"
								+ "<p>"
								+ "Copyright (c) 2011, Johannes Rosenberger <jo.rosenberger@gmx-topmail.de><br>"
								+ "Please look up 'http://www.opensource.org/licenses/bsd-3-clause'"
								+ "for the license template."
								+ "</p>"
								+ "</body>"
								+ "</html>";
							try {
								/*HttpURLConnection c = new HttpURLConnection(
								  new URL(
								  "http://www.opensource.org/licenses/bsd-3-clause"),
								  "www.opensource.org", 80);
								  c.connect();*/
								InputStream netIn = new URL(
										"http://www.opensource.org/licenses/bsd-3-clause").openStream();
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(netIn));
								String body = new String();
								boolean hasStarted = false;
								boolean isFinished = false;
								while (!isFinished) {
									String line = reader.readLine();
									if (line.contains("Copyright"))
										hasStarted = true;
									if (hasStarted) {
										if (line.contains("</div>"))
											isFinished = true;
										else
											body = body + "\n" + line;
									}
								}
								license.setText("<html><head></head><body><p>"
										+ body.replace("&lt;YEAR&gt;", "2011")
										.replace("&lt;OWNER&gt;",
											"Johannes Rosenberger &lt;jo.rosenberger@gmx-topmail.de&gt;")
										.replace("Neither the name of the &lt;ORGANIZATION&gt;"
											+ "nor the names of its contributors may be",
											"The names of its contributors may not be")
										+ "<p>Template: <a href=\"http://www.opensource.org/"
										+ "licenses/bsd-3-clause\">"
										+ "http://www.opensource.org/licenses/bsd-3-clause</a>"
										+ "</p></body></html>");
								license.addHyperlinkListener(new HyperlinkListener() {

									@Override
									public void hyperlinkUpdate(HyperlinkEvent e) {
										try {
											if (e.getEventType().equals(
													EventType.ACTIVATED)) {
												license.setPage(e.getURL());
													}
										} catch (IOException e1) {
											e1.printStackTrace();
										}
									}
								});
								/*} catch (FileNotFoundException e11) {
								  license.setText(notFound);
								  e11.printStackTrace();*/
						} catch (IOException e11) {
							license.setText(notFound.replace("found",
										"read"));
							e11.printStackTrace();
						}
						e1.printStackTrace();
						}

						licenseWindow.add(new JScrollPane(license));
						licenseWindow.pack();
						licenseWindow.setMinimumSize(new Dimension(700, 480));
						licenseWindow.setSize(licenseWindow.getMinimumSize());
						licenseWindow.setVisible(true);
					} else if (e.getDescription().equals("apacheLicense")) {
						final JFrame aplWindow = new JFrame("Apache License 2.0 - " + espressoName);
						final JTextPane aplPane = new JTextPane();
						aplPane.setEditable(false);
						try {
							aplPane.setContentType("text/plain");
							aplPane.setText(CharsetSensitiveFileToStringReader.readFile(apacheLicensePath));
						} catch (Exception e1) {
							aplPane.setContentType("text/html");
							String notFound = "<html>"
								+ "<head>"
								+ "<title>Apache License 2.0 not found</title>"
								+ "</head>"
								+ "<body>"
								+ "<p>"
								+ "FATAL ERROR: The license file could not be found!"
								+ "</p>"
								+ "<p>"
								+ "Please look it up on 'http://www.apache.org/licenses/LICENSE-2.0'."
								+ "</p>"
								+ "</body></html>";
							try {
								aplPane.setPage("http://www.apache.org/licenses/LICENSE-2.0");
								aplPane.addHyperlinkListener(new HyperlinkListener() {

									@Override
									public void hyperlinkUpdate(HyperlinkEvent he) {
										try {
											if (he.getEventType().equals(
													EventType.ACTIVATED)) {
												aplWindow.setExtendedState(
													JFrame.MAXIMIZED_BOTH);
												aplPane.setPage(he.getURL());
													}
										} catch (IOException ioe) {
											ioe.printStackTrace();
										}
									}
								});
							} catch (IOException e2) {
								aplPane.setText(notFound);
							}
						}
						aplWindow.add(new JScrollPane(aplPane));
						aplWindow.pack();
						aplWindow.setMinimumSize(new Dimension(700, 480));
						aplWindow.setSize(aplWindow.getMinimumSize());
						aplWindow.setVisible(true);
					} else if (e.getDescription().equals("mpl")) {
						final JFrame mplWindow = new JFrame("MPLv1.1 - " + espressoName);
						final JTextPane mplPane = new JTextPane();
						mplPane.setEditable(false);
						try {
							mplPane.setContentType("text/plain");
							mplPane.setText(CharsetSensitiveFileToStringReader.readFile(mplPath));
						} catch (Exception e1) {
							mplPane.setContentType("text/html");
							String notFound = "<html>"
								+ "<head>"
								+ "<title>MPL not found</title>"
								+ "</head>"
								+ "<body>"
								+ "<p>"
								+ "FATAL ERROR: The license file could not be found!"
								+ "</p>"
								+ "<p>"
								+ "Please look it up on 'http://www.mozilla.org/MPL/1.1'."
								+ "</p>"
								+ "</body></html>";
							try {
								mplPane.setPage("http://www.mozilla.org/MPL/1.1/");
								mplPane.addHyperlinkListener(new HyperlinkListener() {

									@Override
									public void hyperlinkUpdate(HyperlinkEvent he) {
										try {
											if (he.getEventType().equals(
													EventType.ACTIVATED)) {
												mplWindow.setExtendedState(
													JFrame.MAXIMIZED_BOTH);
												mplPane.setPage(he.getURL());
													}
										} catch (IOException ioe) {
											ioe.printStackTrace();
										}
									}
								});
							} catch (IOException e2) {
								mplPane.setText(notFound);
							}
						}
						mplWindow.add(new JScrollPane(mplPane));
						mplWindow.pack();
						mplWindow.setMinimumSize(new Dimension(700, 480));
						mplWindow.setSize(mplWindow.getMinimumSize());
						mplWindow.setVisible(true);
					}
				}
			}
		});
		aboutPanel.add(aboutPane);
		aboutDialog.add(aboutPanel);
		// d.setSize(600, 450);
		// licenseHintWindow.setMinimumSize(new Dimension(700, 480));
		aboutDialog.pack();
		aboutDialog.setVisible(true);
	}
}

// vim: foldmethod=syntax
