package nor;

import org.fife.ui.autocomplete.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.io.*;
import java.util.Date;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
/******************************************************************************/

public class Norns implements ActionListener, MenuConstants {

  public JFrame frame;
  public RSyntaxTextArea textArea;
  public JLabel statusBar;
  private JLabel textIndicator;
  private JPanel panel;

  private String fileName = "Untitled";
  private boolean saved = true;
  private String applicationName = "Norns";

  private String searchString, replaceString;
  private int lastSearchIndex;

  public FileOperation fileHandler;
  private FontChooser fontDialog = null;
  private FindDialog findReplaceDialog = null;
  private JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem, replaceItem, selectAllItem;
/******************************************************************************/
  //Konstruktor
  public Norns() {
    //Beállítjuk az ablakot.
  	frame = new JFrame(fileName + " - " + applicationName);
  	textArea = new RSyntaxTextArea(30,60);
    textArea.setCodeFoldingEnabled(true);
  	statusBar = new JLabel("   1:1", JLabel.LEFT);
    textIndicator = new JLabel("Plain   ", JLabel.RIGHT);
    panel = new JPanel(new GridLayout(1, 2));
    panel.add(statusBar);
    panel.add(textIndicator);
    frame.add(panel, BorderLayout.SOUTH);
  	frame.add(new RTextScrollPane(textArea), BorderLayout.CENTER);
  	createMenuBar(frame);
  	frame.pack();
  	frame.setLocation(100,50);
  	frame.setVisible(true);
  	frame.setLocation(150,50);
  	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

  	fileHandler = new FileOperation(this);

    //Csinálunk egy Listenert arra, hogy tudjuk, hányadik sorban és oszlopban van
    //a kurzorunk.
    CaretListener caret = new CaretListener() {
    	public void caretUpdate(CaretEvent e) {
    		int lineNumber = 0, column = 0, pos = 0;

    		try {
          //Lekérdezzük a pozíciókat.
    			pos = textArea.getCaretPosition();
    			lineNumber = textArea.getLineOfOffset(pos);
    			column = pos - textArea.getLineStartOffset(lineNumber);
    		} catch (Exception excp) {}
        //Ha nincs bevitt szöveg, akkor a nulla, nullán áll.
    		if(textArea.getText().length() == 0){
    			lineNumber = 0;
    			column = 0;
    		}
        //Megjelenítjük a bal alsó sarokban a sor és oszlop számot.
    		statusBar.setText("   " + (lineNumber+1) + ":" + (column + 1));
    	}
  	};

  	textArea.addCaretListener(caret);

  	DocumentListener myListener = new DocumentListener() {

      ////Ha valamit változott a kódban, akkor változik a mentés állpota.
  		public void changedUpdate(DocumentEvent e){fileHandler.setSave(false);}

      //Ha valamit kitöröltek a kódból, akkor változik a mentés állpota.
  		public void removeUpdate(DocumentEvent e){fileHandler.setSave(false);}

      //Ha valamit beillesztettek a kódba, akkor változik a mentés állpota.
  		public void insertUpdate(DocumentEvent e){fileHandler.setSave(false);}
  	};

	  textArea.getDocument().addDocumentListener(myListener);

    //Akkor válik elérhetővé, mikor a felhasználó nem az x-el akarja bezárni a
    //programot.
  	WindowListener frameClose = new WindowAdapter() {
  		public void windowClosing(WindowEvent we) {
  			if(fileHandler.confirmSave())System.exit(0);
  		}
  	};

	  frame.addWindowListener(frameClose);
    CompletionProvider provider = createCompletionProvider();
    AutoCompletion ac = new AutoCompletion(provider);
    ac.install(textArea);
    ac.setAutoActivationEnabled(true);
    ac.setAutoCompleteEnabled(true);
    ac.setAutoActivationDelay(10);
  }

/******************************************************************************/
  public CompletionProvider createCompletionProvider() {
     //Ez a CompletionProvider legegyszerűbb fajtája, mert ez nem tesz
     //megkülönböztetést a nyelvek között. Vagyis csak azzal foglalkozik
     //amit adtunk neki.
     DefaultCompletionProvider provider = new DefaultCompletionProvider();

     try{
       BufferedReader br = new BufferedReader(new FileReader("./Files/Code.txt"));
       ArrayList<String> words = new ArrayList<String>();
       String str;
       while ((str = br.readLine()) != null){
         words.add(str);
       }

       //Szavakat adunk hozzá. De jelenleg még csak a Java nyelvet támogatja.
       for (String s : words){
         provider.addCompletion(new BasicCompletion(provider, s));
       }
     } catch (IOException ex) {
       ex.printStackTrace();
     }
     return provider;
  }

/******************************************************************************/
  //Ezzel lehet a szöveg színezését beállítani.
  public void setSyntax(String ext){
		switch(ext){
			case "actionscript": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT); break;
			case "asm":	textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86); break;
			case "bbcode": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_BBCODE); break;
			case "c": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C); break;
			case "clojure": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CLOJURE); break;
			case "cpp": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS); break;
			case "cs": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP); break;
			case "css": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS); break;
			case "delphi": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DELPHI); break;
			case "dtd": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DTD); break;
			case "fortran": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_FORTRAN); break;
			case "groovy": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY); break;
			case "html": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML); break;
			case "java": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA); break;
			case "javascript": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT); break;
			case "json": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON); break;
			case "jsp": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSP); break;
			case "latex": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LATEX); break;
			case "lisp": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LISP); break;
			case "lua": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA); break;
			case "makefile": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MAKEFILE); break;
			case "mxml": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MXML); break;
			case "nsis": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NSIS); break;
			case "perl": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PERL); break;
			case "php": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP); break;
			case "properties": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE); break;
			case "python": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON); break;
			case "ruby": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY); break;
			case "sas": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SAS); break;
			case "scala": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA); break;
			case "sql": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL); break;
			case "tcl": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL); break;
			case "unix": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL); break;
			case "vb": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC); break;
			case "bat": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH); break;
			case "xml": textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML); break;
			default: textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE); break;
		}
	}

/******************************************************************************/
  //Ezen keresztül tudja az ablak, hogy melyik eseményre mivel reagáljon.
  public void actionPerformed(ActionEvent ev) {
    String cmdText = ev.getActionCommand(); //Visszaad egy Stringet, hogy éppen mi futott le.

    switch (cmdText) {
      case fileNew:
        //Mikor új fájlt nyitunk meg.
        fileHandler.newFile();
        break;
      case fileOpen:
        //Mikor már egy létező fájlt nyitunk meg.
        fileHandler.openFile();
        String fileName = fileHandler.getFileName();
        fileName = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        setSyntax(fileName);
        textIndicator.setText(fileName + "   ");
        break;
      case fileSave:
        //Mikor mentünk egy fájlra.
        fileHandler.saveThisFile();
        break;
      case fileSaveAs:
        //Mikor másképp akarunk menteni.
        fileHandler.saveAsFile();
        break;
      case fileExit:
        //Ha a menün keresztül lépünk ki a programból.
        if(fileHandler.confirmSave()){
          System.exit(0);
        }
        break;
      case editCut:
        //Ha kiszeretnénk vágni egy részletet.
        textArea.cut();
        break;
      case editCopy:
        //Ha kiszeretnénk másolni egy részletet.
        textArea.copy();
        break;
      case editPaste:
        //Ha beakarunk illeszteni egy részletet.
        textArea.paste();
        break;
      case editDelete:
        //Ha törölni akurnk egy részletet.
        textArea.replaceSelection("");
        break;
      case editFind:
        //Ha rászeretnénk keresni egy kifejezésre.
        //Ha nincs szöveg, akkor nem lehet miben keresni.
        if(Norns.this.textArea.getText().length() == 0)
          return;

        //Ha nem üres a textArea és még nem jött létre az új abbalak, akkor megteremti.
        if(findReplaceDialog == null)
          findReplaceDialog = new FindDialog(Norns.this.textArea);

        findReplaceDialog.showDialog(Norns.this.frame, true);
        break;
      case editFindNext:
        //A következő elem megtalálása.
        if(Norns.this.textArea.getText().length() == 0)
      		return;	// text box have no text

      	if(findReplaceDialog == null)
      		statusBar.setText("Nothing to search for, use Find option of Edit Menu first!");
      	else
      		findReplaceDialog.findNextWithSelection();
        break;
      case editReplace:
        //Helyetessíti a megtalált szöveg a megadottal.
        if(Norns.this.textArea.getText().length() == 0)
      		return;

      	if(findReplaceDialog == null)
      		findReplaceDialog = new FindDialog(Norns.this.textArea);

      	findReplaceDialog.showDialog(Norns.this.frame, false);//replace
        break;
      case editSelectAll:
        //Kiválaszt minden karaktert a textArea-ból.
        textArea.selectAll();
      case editTimeDate:
        //Betudunk szúrni időpontot, hogy mikor hol jártunk a kódban,
        textArea.insert(new Date().toString(),textArea.getSelectionStart());
        break;
      case formatWordWrap:
        //Sortördelést is tudunk választani.
        JCheckBoxMenuItem temp = (JCheckBoxMenuItem)ev.getSource();
        textArea.setLineWrap(temp.isSelected());
        break;
      case formatFont:
        //Tudjuk állítani a kód betűtípusát is.
        if(fontDialog == null)
          fontDialog = new FontChooser(textArea.getFont());

        if(fontDialog.showDialog(Norns.this.frame, "Choose a font"))
          Norns.this.textArea.setFont(fontDialog.createFont());
        break;
      case viewStatusBar:
        //Betudjuk állítani, hogy szeretnénk-e látni a status bar-t.
        JCheckBoxMenuItem tem = (JCheckBoxMenuItem)ev.getSource();
        statusBar.setVisible(tem.isSelected());
        break;
      case helpAboutNorns:
        //Leírás a Norns-ról.
        JOptionPane.showMessageDialog(Norns.this.frame,aboutText,"Dedicated to you!",JOptionPane.INFORMATION_MESSAGE);
      default:
        //Egyébként, ha rossz billenytű kombót nyom le ezt adja vissza.
        statusBar.setText("This " + cmdText + " command is yet to be implemented");
        break;
    };
  }

/******************************************************************************/
  //Létrehozzuk a menü elemeket ezzel.
  public JMenuItem createMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
  	JMenuItem temp = new JMenuItem(s, key);
  	temp.addActionListener(al);
  	toMenu.add(temp);

	  return temp;
  }

/******************************************************************************/
  //Gyors gombbal is elérhetp menü elem.
  public JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, ActionListener al) {
  	JMenuItem temp = new JMenuItem(s, key);
  	temp.addActionListener(al);
  	temp.setAccelerator(KeyStroke.getKeyStroke(aclKey,ActionEvent.CTRL_MASK));
  	toMenu.add(temp);

  	return temp;
  }

/******************************************************************************/
  //Egy olyan menü elem létrehozása, amley tartalamaz ilyen jelölő nézetet.
  public JCheckBoxMenuItem createCheckBoxMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
  	JCheckBoxMenuItem temp = new JCheckBoxMenuItem(s);
  	temp.setMnemonic(key);
  	temp.addActionListener(al);
  	temp.setSelected(false);
  	toMenu.add(temp);

  	return temp;
  }

/******************************************************************************/
  //Egy menü pont létrehozása.
  public JMenu createMenu(String s, int key, JMenuBar toMenuBar) {
  	JMenu temp = new JMenu(s);
  	temp.setMnemonic(key);
  	toMenuBar.add(temp);
  	return temp;
  }

/******************************************************************************/
  //Menü sáv létrehozása.
  public void createMenuBar(JFrame frame) {
  	JMenuBar mb = new JMenuBar();
  	JMenuItem temp;

    //Felvesszük a fő menü pontokat.
  	JMenu fileMenu = createMenu(fileText,KeyEvent.VK_F,mb);
  	JMenu editMenu = createMenu(editText,KeyEvent.VK_E,mb);
  	JMenu formatMenu = createMenu(formatText,KeyEvent.VK_O,mb);
  	JMenu viewMenu = createMenu(viewText,KeyEvent.VK_V,mb);
  	JMenu helpMenu = createMenu(helpText,KeyEvent.VK_H,mb);

    //A fileMenu pontnak adunk elemeket.
  	createMenuItem(fileNew, KeyEvent.VK_N, fileMenu, KeyEvent.VK_N, this);
  	createMenuItem(fileOpen, KeyEvent.VK_O, fileMenu, KeyEvent.VK_O, this);
  	createMenuItem(fileSave, KeyEvent.VK_S, fileMenu, KeyEvent.VK_S, this);
  	createMenuItem(fileSaveAs, KeyEvent.VK_A, fileMenu, this);
  	fileMenu.addSeparator();
  	createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);

    //Az editMenu pontnak adunk elemeket.
  	temp = createMenuItem(editUndo, KeyEvent.VK_U, editMenu, KeyEvent.VK_Z, this);
  	temp.setEnabled(false);
  	editMenu.addSeparator();

  	cutItem = createMenuItem(editCut, KeyEvent.VK_T, editMenu, KeyEvent.VK_X, this);
  	copyItem = createMenuItem(editCopy, KeyEvent.VK_C, editMenu, KeyEvent.VK_C, this);
  	createMenuItem(editPaste, KeyEvent.VK_P, editMenu, KeyEvent.VK_V, this);
  	deleteItem = createMenuItem(editDelete, KeyEvent.VK_L, editMenu, this);
  	deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
  	editMenu.addSeparator();

  	findItem = createMenuItem(editFind, KeyEvent.VK_F, editMenu, KeyEvent.VK_F, this);
  	findNextItem = createMenuItem(editFindNext, KeyEvent.VK_N, editMenu, this);
  	findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
  	replaceItem = createMenuItem(editReplace, KeyEvent.VK_R, editMenu, KeyEvent.VK_H, this);
  	editMenu.addSeparator();

  	selectAllItem = createMenuItem(editSelectAll, KeyEvent.VK_A, editMenu, KeyEvent.VK_A, this);
  	createMenuItem(editTimeDate, KeyEvent.VK_D, editMenu, this).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

    //A formatMenu pontnak adunk elemeket.
  	createCheckBoxMenuItem(formatWordWrap, KeyEvent.VK_W, formatMenu, this);
  	createMenuItem(formatFont, KeyEvent.VK_F, formatMenu, this);

    //a viewMenu pontnak adunk elemeket.
  	createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);
  	LookAndFeelMenu.createLookAndFeelMenuItem(viewMenu, this.frame);
    ChangeSyntaxStyle.createChangeSyntaxStyle(viewMenu, this.textArea);

    //A helpMenu pontnak adunk elemeket.
  	temp = createMenuItem(helpHelpTopic, KeyEvent.VK_H, helpMenu, this);
  	temp.setEnabled(false);
  	helpMenu.addSeparator();
  	createMenuItem(helpAboutNorns, KeyEvent.VK_A, helpMenu, this);

    //Csinálunk egy listenert azokra a menu elemekre, amelyek nem mindig
    //elérhetőek.
  	MenuListener editMenuListener = new MenuListener() {
  	   public void menuSelected(MenuEvent evvvv){
          //Ha nincs még szöveg a textArea-ban, akkor minden disabled.
      		if(Norns.this.textArea.getText().length() == 0) {
        		findItem.setEnabled(false);
        		findNextItem.setEnabled(false);
        		replaceItem.setEnabled(false);
        		selectAllItem.setEnabled(false);
      		}
      		else {
            //Egyébként minden elérhető
        		findItem.setEnabled(true);
        		findNextItem.setEnabled(true);
        		replaceItem.setEnabled(true);
        		selectAllItem.setEnabled(true);
      		}
          //Ha nincs kijelölés, akkor ezek nem elérhetőek.
      	  if(Norns.this.textArea.getSelectionStart() == textArea.getSelectionEnd()) {
        		cutItem.setEnabled(false);
        		copyItem.setEnabled(false);
        		deleteItem.setEnabled(false);
      		} else {
            //Amúgy igen.
        		cutItem.setEnabled(true);
        		copyItem.setEnabled(true);
        		deleteItem.setEnabled(true);
      		}
      }
      public void menuDeselected(MenuEvent evvvv){}
      public void menuCanceled(MenuEvent evvvv){}
  	};

  	editMenu.addMenuListener(editMenuListener);
  	frame.setJMenuBar(mb);
  }

/******************************************************************************/
  public static void main(String[] args){
    SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
            new Norns();
        }
    });
  }
/******************************************************************************/
}
