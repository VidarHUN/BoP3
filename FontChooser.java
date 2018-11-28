package nor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
/******************************************************************************/

public class FontChooser extends JPanel {

	private static final long serialVersionUID = 66666;

	private Font thisFont;

	private JList jFace, jStyle, jSize;

	private JDialog dialog;
	private JButton okButton;

	private RSyntaxTextArea textArea;

	private boolean ok;
/******************************************************************************/
	public FontChooser(Font withFont) {
		thisFont = withFont;

		//Megnézzük, hogy milyen típusokat támogat a rendszerünk.
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		jFace = new JList<String>(fontNames);
		//Az első elem lesz az alapértelmezett.
		jFace.setSelectedIndex(0);

		//Ha kiválasztunk valamit, akkor változzon.
		jFace.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent ev){
				textArea.setFont(createFont());
			}
		});

		//A szöveg formázásának paraméterei.
		String[] fontStyles = {"Regular","Italic","Bold","Bold Italic"};
		jStyle = new JList<String>(fontStyles);
		jStyle.setSelectedIndex(0);

		jStyle.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent ev){
				textArea.setFont(createFont());
			}
		});

		//Felvesszük a lehetséges méreteket.
		String[] fontSizes = new String[30];
		for(int j = 0; j < 30; j++)
			fontSizes[j] = new String(10 + j * 2 + "");
		jSize = new JList<String>(fontSizes);
		jSize.setSelectedIndex(0);

		jSize.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent ev){
				textArea.setFont(createFont());
			}
		});

		//Kell egy olyan panel, ahol a címeket tároljuk.
		JPanel jpLabel = new JPanel();
		jpLabel.setLayout(new GridLayout(1, 3));

		jpLabel.add(new JLabel("Font", JLabel.CENTER));
		jpLabel.add(new JLabel("Font Style", JLabel.CENTER));
		jpLabel.add(new JLabel("Size", JLabel.CENTER));

		//Egy panel, ahol a három beállítási lehetőség lesz és az azokhoz
		//rendelt scrollpane
		JPanel jpList = new JPanel();
		jpList.setLayout(new GridLayout(1, 3));

		jpList.add(new JScrollPane(jFace));
		jpList.add(new JScrollPane(jStyle));
		jpList.add(new JScrollPane(jSize));

		okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");

		//Ha valami változott, akkor azt elmenjük.
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ok = true;
				FontChooser.this.thisFont = FontChooser.this.createFont();
				dialog.setVisible(false);
			}
		});

		//Ha nem akarunk semmit, akkor elrejtjük az ablakot.
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				dialog.setVisible(false);
			}
		});

		//Belepakoljuk egy panelba a gombokat.
		JPanel jpButton = new JPanel();
		jpButton.setLayout(new FlowLayout());
		jpButton.add(okButton);
		jpButton.add(cancelButton);

		//Kell egy olyan terület is, ami egy sablon szöveget ad meg, annak érdekében,
		//hogy láthassuk rögtön a változásokat.
		textArea = new RSyntaxTextArea(5, 30);
		JPanel jpTextField = new JPanel();
		jpTextField.add(new JScrollPane(textArea));

		//Elhelyezzük az eddig létrehozott panelokat.
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(2, 1));
		centerPanel.add(jpList);
		centerPanel.add(jpTextField);

		//Az így létrejött paneleket elrendezzük egy BorderLayout-os megoldásban.
		setLayout(new BorderLayout());
		add(jpLabel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(jpButton, BorderLayout.SOUTH);

		//Majd megvalósítjuk a példát.
		textArea.setFont(thisFont);
		textArea.append("\nA quick brown fox jumps over the lazy dog.");
		textArea.append("\n0123456789");
		textArea.append("\n~!@#$%^&*()_+|?><\n");

	}
/******************************************************************************/
	//Létrehozunk egy betűtípust.
	public Font createFont() {
		Font fnt = thisFont;
		int fontstyle = Font.PLAIN;
		int x = jStyle.getSelectedIndex();

		//Asszerint változik, hogy a felhasználó hogy stílust szeretne.
		switch(x) {
			case 0:
				fontstyle = Font.PLAIN;
				break;
			case 1:
				fontstyle = Font.ITALIC;
				break;
			case 2:
				fontstyle = Font.BOLD;
				break;
			case 3:
				fontstyle = Font.BOLD+Font.ITALIC;
				break;
			default:
				break;
		}

		//Lekérjük a betűméretet.
		int fontsize = Integer.parseInt((String)jSize.getSelectedValue());
		//Milyen típust szeretne.
		String fontname = (String)jFace.getSelectedValue();

		//Létrehozzuk a betűtípust.
		fnt = new Font(fontname,fontstyle,fontsize);

		return fnt;
	}
/******************************************************************************/
	//Arra szolgál, hogy létrejöjjön a dialógus ablak.
	public boolean showDialog(Component parent, String title) {
		ok = false;

		Frame owner = null;
		if(parent instanceof Frame)
			owner = (Frame)parent;
		else
			owner = (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);
		if(dialog == null || dialog.getOwner() != owner){
			dialog = new JDialog(owner, true);
			dialog.add(this);
			dialog.getRootPane().setDefaultButton(okButton);
			dialog.setSize(400, 325);
		}

		dialog.setTitle(title);
		dialog.setVisible(true);

		return ok;
	}
/******************************************************************************/
}
