package nor;

import java.io.*;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/******************************************************************************/

public class FileOperation {
	private Norns nor;

	private boolean saved;
	private boolean newFileFlag;
	private String fileName;
	private String applicationTitle = "Norns";

	private File fileRef;
	private JFileChooser chooser;

  //Visszatér azzal, az információval, hogy elvan-e mentve a dokumentum.
	public boolean isSave(){return saved;}

  //Ha majd rányomunk a mentés gombra, akkor ez állítja be, hogy mentettünk.
	public void setSave(boolean saved){this.saved = saved;}

  //Lekérdezi a megnyitott file-nak a nevét.
	public String getFileName(){return new String(fileName);}

  //Betudjuk állítani, hogy a file-nak mi legyen a neve.
	public void setFileName(String fileName){this.fileName = new String(fileName);}
/******************************************************************************/
  //Konstruktor, ami egy Norns típust kap paraméteréül.
	public FileOperation(Norns nor) {
		this.nor = nor;

    //Mikor létrehozunk egy új doksit, akkor az első alkalommal mentve van a doksi.
		saved = true;

    //És mivel ez egy új fájl, ezért a flag-et is beállítjuk rá.
		newFileFlag = true;
		fileName = new String("Untitled");
		fileRef = new File(fileName);
		this.nor.frame.setTitle(fileName + " - " + applicationTitle); // Untitled - Norns

    //Ez létrehoz egy olyan ablakot, ahol a felhasználó fájlokat tud kiválasztani
    //a megnyitásra.
		chooser = new JFileChooser();

    //Most csak java fájlokat tud megnyitni.
		//chooser.addChoosableFileFilter(new FileTypeFilte(".java","Java Source Files(*.java)"));

    //Beállítjuk a jelenlegi mappát.
		chooser.setCurrentDirectory(new File("."));
	}
/******************************************************************************/
  //Ezzel lehet elmenteni a fájlokat.
	public boolean saveFile(File temp) {
		FileWriter fileOut = null;

    //Megpróbáljuk kiírni megnyitni a fájlt és beleírni.
    try {
		    fileOut = new FileWriter(temp);
		    fileOut.write(nor.textArea.getText());
		} catch(IOException ioe){
      //Ha nem sikerült, akkor frissíti a saved-t és a fileName-t.
      updateStatus(temp,false);
      return false;
    } finally {
      try{
        fileOut.close();
      } catch(IOException excp) {}
    }

    //Ha sikerül, akkor a saved = true és a fileName-t is beállítja.
		updateStatus(temp,true);
		return true;
	}
/******************************************************************************/
  //Ezzel lehet menteni, mikor egy létező fájlba írtunk bele.
	public boolean saveThisFile() {
    //Ha nem új fájlról van szó csak, akkor lehet így elmenteni.
		if (!newFileFlag) {
      return saveFile(fileRef);
    }
		return saveAsFile();
	}
/******************************************************************************/
  //Ezzel, akkor lehet menteni, mikor egy másik formátumban vagy más helyre
  //akarunk menteni.
	public boolean saveAsFile() {
		File temp = null;

		chooser.setDialogTitle("Save As...");   //Beállítja a dialógus ablak címét.
		chooser.setApproveButtonText("Save Now"); //A jóváhagyás gomb beállítása.
		chooser.setApproveButtonMnemonic(KeyEvent.VK_S);  //Gyosbillentyű ctrl-s

    //Ha a mentés gomb felé visszük a kurzort akkor ez a szöveg fog megjelenni.
		chooser.setApproveButtonToolTipText("Click me to save!");

		do {
      //Ha ezen az ablakon rányomunk a mentésre, akor egy JFileChooser.APPROVE_OPTION
      //értékkel vagyis egy int-el kell visszatérnie. Ha még nem mentettük, akkor
      //false értékkel fog visszatérni.
	    if (chooser.showSaveDialog(this.nor.frame) != JFileChooser.APPROVE_OPTION)
	      return false;

      //A temp-be belerakja a kiválaszott file-t.
  		temp = chooser.getSelectedFile();

      //Ha nem létezik, akkor lépjen ki a ciklusból.
  		if (!temp.exists())
        break;
      //Egyébként meg ha egy olyan helyre akarunk menteni, ahol már létezik egy
      //ilyen fájl, akkor kérdezze meg, hogy kiszeretnénk-e cserélni. Erre létrehoz
      //egy olyan kommunikációs ablakot, ami kiírja az elérési útvonalát és a
      //kérdést. Erre csak Yes vagy No-val tudunk válasszolni.
  		if (JOptionPane.showConfirmDialog(this.nor.frame,"<html>" + temp.getPath() +
        " already exists.<br>Do you want to replace it?<html>",
  			"Save As", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION)
        //Ha igenel válaszolt lépjen ki a ciklusból.
  			break;
		} while(true);

    //Elmentjük a file-t.
		return saveFile(temp);
	}
/******************************************************************************/
  //Ezzel lehet megnyitni a fájlokat.
	public boolean openFile(File temp) {
		FileInputStream fileIn = null;
		BufferedReader dataIn = null;

		try {
      //Flie beolvasás.
  		fileIn = new FileInputStream(temp);
  		dataIn = new BufferedReader(new InputStreamReader(fileIn));
  		String str = " ";
      //Olvasunk egészen, addig amíg ki nem fogyunk a sorokból és folyamatosan
      //hozzáadjuk a textArea-hoz a beolvasott sorokat egy sortöréssel megtűzve.
  		while (str != null) {
      		str = dataIn.readLine();
      		if(str == null)
      		  break;
      		this.nor.textArea.append(str + "\n");
    		}
		} catch(IOException ioe) {
      //Ha valami hiba van, akkor frissítünk.
      updateStatus(temp, false);
      return false;
    } finally{
      try{
        //A végén bezárjuk a fájlokat.
        dataIn.close();
        fileIn.close();
      } catch (IOException excp) {}
    }
		updateStatus(temp,true);
    //A dokumentum elejéré teszi a kurzort.
		this.nor.textArea.setCaretPosition(0);
		return true;
	}
/******************************************************************************/
  //Ezzel lehet hozzuk létre a fájlok beolvasásához használt dialógust.
	public void openFile() {
    //Ha van megnyitva egy fájl és az nincs elmentve, mikor mi megakarunk
    //nyitni egy fájlt, akkor ez rákérdez.
		if(!confirmSave())
      return;

    //Beállítjuk a dialógusnak az attribútumait.
		chooser.setDialogTitle("Open File...");
		chooser.setApproveButtonText("Open this");
		chooser.setApproveButtonMnemonic(KeyEvent.VK_O); //ctrl + o -val lehet megnyitni.
		chooser.setApproveButtonToolTipText("Click me to open the selected file.!");


		File temp = null;
		do {
      //Ha már megnyitottuk a fájlt, akkor visszatér.
  		if(chooser.showOpenDialog(this.nor.frame) != JFileChooser.APPROVE_OPTION)
  			return;

      //Átadjuk a kiválaszott fájlt.
  		temp = chooser.getSelectedFile();

      //Ha létezik, akkor töri a ciklust.
  		if(temp.exists()){
				break;
			}

      //Ha nem található ilyen fájl, akkor térjen vissza egy üzenet dialógussal.
  		JOptionPane.showMessageDialog(this.nor.frame,
  			"<html>"+temp.getName()+"<br>file not found.<br>"+
  			"Please verify the correct file name was given.<html>",
  			"Open",	JOptionPane.INFORMATION_MESSAGE);

		} while(true);

    //Ha nem választunk fájlt, akkor egy újat hoz létre.
		this.nor.textArea.setText("");

		if(!openFile(temp)) {
			fileName = "Untitled";
      saved = true;
			this.nor.frame.setTitle(fileName + " - " + applicationTitle);
		}
    //Ha a fájlt írható, akkor beállítjuk a newFileFlag-t igazra.
		if(!temp.canWrite())
			newFileFlag = true;
	}
/******************************************************************************/
  //Visszajelzést add arról, hogy sikerült-e menteni vagy megnyitni egy fájlt.
	public void updateStatus(File temp, boolean saved) {
		if (saved) {
      //Ha menthető, akkor beállítja az értékeket.
  		this.saved = true;
  		fileName = new String(temp.getName());
      //Ha nem írható a fájl, akkor csak olvashatóban nyithatjuk meg.
  		if (!temp.canWrite()) {
        fileName += "(Read only)";
        newFileFlag = true;
      }
  		fileRef = temp;
  		nor.frame.setTitle(fileName + " - " + applicationTitle);
  		nor.statusBar.setText("File : " + temp.getPath() + " saved/opened successfully.");
  		newFileFlag = false;
  	} else {
		  nor.statusBar.setText("Failed to save/open : "+temp.getPath());
		}
	}
/******************************************************************************/
	public boolean confirmSave() {
		String strMsg = "<html>The text in the " + fileName + " file has been changed.<br>" + "Do you want to save the changes?<html>";

    //Ha nem lett elmentve, akkor felhoz egy dialógust, ami megkérdezi, hogy
    //menti-e a változtatásokat és van 3 opció, hogy kilép, nem, igen.
    if(!saved) {
      //Létre hozzuk a megerősítő dialógust, a visszatérési értékétől függően
      //járunk el.
  		int x = JOptionPane.showConfirmDialog(this.nor.frame, strMsg, applicationTitle, JOptionPane.YES_NO_CANCEL_OPTION);

      //Ha a cancel opciót választotta.
  		if (x == JOptionPane.CANCEL_OPTION)
        return false;

      //Ha az igen opciót választotta.
  		if (x == JOptionPane.YES_OPTION && !saveAsFile())
        return false;
  	}
		return true;
	}
/******************************************************************************/
  //Üres dokumentum létrehozása.
	public void newFile(){
    //Ha van megnyitva egy fájl és az nincs elmentve, mikor mi megakarunk
    //nyitni egy fájlt, akkor ez rákérdez.
		if(!confirmSave())
      return;

    //Az üres dokumentum beállításai.
		this.nor.textArea.setText("");
		fileName = new String("Untitled");
		fileRef = new File(fileName);
		saved = true;
		newFileFlag = true;
		this.nor.frame.setTitle(fileName + " - " + applicationTitle);
	}
/******************************************************************************/
}
