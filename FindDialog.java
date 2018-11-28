package nor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

/******************************************************************************/
public class FindDialog extends JPanel implements ActionListener {
	private static final long serialVersionUID = 555555;

	//Felvesszül a szükséges változókat.
	private RSyntaxTextArea textArea;
	private int lastIndex;
	private JLabel replaceLabel;

	//Ez egy fx-es elem. Azért kell mert ehhez lehet adni textListener-t.
	private TextField findWhat;
	private JTextField replaceWith;

	private JCheckBox matchCase;

	private JRadioButton up, down;

	private JButton findNextButton, replaceButton, replaceAllButton, cancelButton;

	private JPanel direction, buttonPanel, findButtonPanel, replaceButtonPanel;

	private JDialog dialog;
/******************************************************************************/

	//A konstruktor, amivel létrehozzuk az egyes ablakokat.
	public FindDialog(RSyntaxTextArea textArea) {
		//Létre hozzuk a komponenseket.
		this.textArea = textArea;	//Átadjuk neki a vizsgálni kívánt területe.

		//A fieldek.
		findWhat = new TextField(20);
		replaceWith = new JTextField(20);

		matchCase = new JCheckBox("Match case");

		//Kereséséi irányok.
		up = new JRadioButton("Up");
		down = new JRadioButton("Down");

		//Csoportba rendezzük gombokat.
		down.setSelected(true); //Alapértelmezetten a lefelé keresés van kipipálva.
		ButtonGroup bg = new ButtonGroup();
		bg.add(up);
		bg.add(down);

		//Létrehozunk egy külön panelt az útiránynak , aminek csinálunk egy
		//ilyen nagyon fancy keretet, amiben a vonalba lesz beleírva a
		//keret neve.
		direction = new JPanel();
		Border etched = BorderFactory.createEtchedBorder(); //Keret
		Border titled = BorderFactory.createTitledBorder(etched, "Direction"); //Keret címe
		direction.setBorder(titled);
		direction.setLayout(new GridLayout(1, 2));
		direction.add(up);
		direction.add(down);

		//Aztán ezt az egészet elhelyezzük az alsó panelen, úgyhogy a
		//matchcase legyen bal oldalon a direction meg a jobb oldalon.
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(1, 2));
		southPanel.add(matchCase);
		southPanel.add(direction);

		//Létrehozzuk a gombokat.
		findNextButton = new JButton("Find Next");
		replaceButton = new JButton("Replace");
		replaceAllButton = new JButton("Replace All");
		cancelButton = new JButton("Cancel");

		//Létrehozunk egy olyan panelt, ami az előbb létrehozott gombokat tárolja.
		replaceButtonPanel = new JPanel();
		replaceButtonPanel.setLayout(new GridLayout(4, 1));
		replaceButtonPanel.add(findNextButton);
		replaceButtonPanel.add(replaceButton);
		replaceButtonPanel.add(replaceAllButton);
		replaceButtonPanel.add(cancelButton);

		//Létrehozzuk a replace ablakot.
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(3, 2));
		textPanel.add(new JLabel("Find what "));
		textPanel.add(findWhat);
		textPanel.add(replaceLabel = new JLabel("Replace With "));
		textPanel.add(replaceWith);

		//Beállítjuk a fő panelt.
		setLayout(new BorderLayout());

		add(textPanel, BorderLayout.CENTER);	//A mezők
		add(replaceButtonPanel, BorderLayout.EAST);	//A gombok
		add(southPanel, BorderLayout.SOUTH);	//A rádió gombok.

		setSize(200, 200);

		//Hozzáadjuk ezt az ActionListener-t
		findNextButton.addActionListener(this);
		replaceButton.addActionListener(this);
		replaceAllButton.addActionListener(this);

		//Ha kilép, akkor eltüntetjük az ablakot.
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				dialog.setVisible(false);
			}
		});

		findWhat.addFocusListener(new FocusAdapter(){
			public void focusLost(FocusEvent te){
				enableDisableButtons();
			}
		});
		findWhat.addTextListener(new TextListener(){
			public void textValueChanged(TextEvent te){
				enableDisableButtons();
			}
		});
	}
/******************************************************************************/
	//Gombok engedélyezésének kérdése.
	public void enableDisableButtons() {
		//Ha még nem írt semmit a mezőbe, akkor minden tiltva van.
		if(findWhat.getText().length() == 0) {
			findNextButton.setEnabled(false);
			replaceButton.setEnabled(false);
			replaceAllButton.setEnabled(false);
		} else {
			//Egyébként, minden mehet.
			findNextButton.setEnabled(true);
			replaceButton.setEnabled(true);
			replaceAllButton.setEnabled(true);
		}
	}
/******************************************************************************/
	//Gomblenyomásokat figyeli.
	public void actionPerformed(ActionEvent ev) {
		if(ev.getSource() == findNextButton)
			findNextWithSelection();
		else if(ev.getSource() == replaceButton)
			replaceNext();
		else if(ev.getSource() == replaceAllButton)
			JOptionPane.showMessageDialog(null, "Total replacements made = " + replaceAllNext());
	}
/******************************************************************************/
	//Következő megtalálása, panel nélkül.
	public int findNext() {
		//Lekérjük a textArea szövegét és, azt hogy mit akarnak megtalálni.
		String s1 = textArea.getText();
		String s2 = findWhat.getText();

		lastIndex = textArea.getCaretPosition(); //Hol van az éppen a kurzor?

		//Hol van a kiválasztott szöveg eleje és vége?
		int selStart = textArea.getSelectionStart();
		int selEnd = textArea.getSelectionEnd();

		//Ha a felfelé keresés van kiválasztva.
		if(up.isSelected()) {
			if(selStart != selEnd)
				lastIndex = selEnd - s2.length() - 1;
			if(!matchCase.isSelected())
				lastIndex = s1.toUpperCase().lastIndexOf(s2.toUpperCase(), lastIndex);
			else
				lastIndex = s1.lastIndexOf(s2, lastIndex);
		} else {
			if(selStart != selEnd)
				lastIndex = selStart+1;
			if(!matchCase.isSelected())
				lastIndex = s1.toUpperCase().indexOf(s2.toUpperCase(), lastIndex);
			else
				lastIndex = s1.indexOf(s2, lastIndex);
		}
		return lastIndex;
	}
/******************************************************************************/
	//Következő megtalálása kijelöléssel.
	public void findNextWithSelection() {
		int idx = findNext();
		//Ha az utolsó index nem egyenlő -1-el.
		if(idx != -1) {
			//Kijelölünk.
			textArea.setSelectionStart(idx);
			textArea.setSelectionEnd(idx + findWhat.getText().length());
		}
		else
			//Ha nem talált semmi, legyen ez az üzenet.
			JOptionPane.showMessageDialog(this, "Cannot find" + " \"" + findWhat.getText()+ "\"", "Find", JOptionPane.INFORMATION_MESSAGE);
	}
/******************************************************************************/
	//A következő szó kicserélése.
	public void replaceNext() {
		//Ha semmi sincs kiválasztva.
		if(textArea.getSelectionStart() == textArea.getSelectionEnd()){
			findNextWithSelection();
			return;
		}

		//Mire keresünk és mi van kiválasztva.
		String searchText = findWhat.getText();
		String temp = textArea.getSelectedText();

		//Megnézzük, hogy a kiválaszott szöveg egyezik a keresettel, majd cserélünk.
		if((matchCase.isSelected() && temp.equals(searchText)) ||
			 (!matchCase.isSelected() && temp.equalsIgnoreCase(searchText)))
			textArea.replaceSelection(replaceWith.getText());

		//Aztán kijelöljük, a következő hasonlót.
		findNextWithSelection();
	}
/******************************************************************************/
	//Ha egyszerre akarunk kicserélni mindent.
	public int replaceAllNext() {
		//Ha felfelé keresés van kiválasztva, akkor felfelé haladunk
		//egyébként le.
		if(up.isSelected())
			textArea.setCaretPosition(textArea.getText().length() - 1);
		else
			textArea.setCaretPosition(0);

		int idx = 0;
		int counter = 0;
		do {
			//Lekérjük a következő ilyen szó indexét. Ha ez -1, akkor off.
			idx = findNext();
			if(idx == -1)
				break;

			counter++;
			//Itt történik meg a csere.
			textArea.replaceRange(replaceWith.getText(), idx, idx + findWhat.getText().length());
		} while(idx != -1);

		//Visszatérünk azzal, hogy darabot cserélt.
		return counter;
	}
/******************************************************************************/
	//Megjelnítjük az ablakot.
	public void showDialog(Component parent, boolean isFind) {
		Frame owner = null;
		//Ha a parent egy pédánya a Frame-nek, akkor kasztolunk.
		if(parent instanceof Frame)
			owner = (Frame)parent;
		else
			//Megkeressük az első Frame osztályt a parent-ben.
			owner = (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);

		//A dialog ablak beállítása.
		if(dialog == null || dialog.getOwner() != owner) {
			dialog = new JDialog(owner, false);
			dialog.add(this);
			//Ez a gomb mindig rajta van.
			dialog.getRootPane().setDefaultButton(findNextButton);
		}

		//Tiltás vagy nem tiltás.
		if(findWhat.getText().length() == 0)
			findNextButton.setEnabled(false);
		else
			findNextButton.setEnabled(true);

		//Ezek a gombok alapjáraton nem látszanak.
		replaceButton.setVisible(false);
		replaceAllButton.setVisible(false);
		replaceWith.setVisible(false);
		replaceLabel.setVisible(false);

		//Sima megjelenés.
		if(isFind) {
			//card.show(buttonPanel,"find");
			dialog.setSize(460, 180);
			dialog.setTitle("Find");
		} else {
			//Mikor már cserélni is akarunk.
			replaceButton.setVisible(true);
			replaceAllButton.setVisible(true);
			replaceWith.setVisible(true);
			replaceLabel.setVisible(true);

			dialog.setSize(450, 200);
			dialog.setTitle("Replace");
		}

		dialog.setVisible(true);
	}
/******************************************************************************/
}
