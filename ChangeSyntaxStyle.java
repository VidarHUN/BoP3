package nor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
/******************************************************************************/

public class ChangeSyntaxStyle {

  //El kell tárulnunk azt, hogy az egyes típusoknak, hol van a megfelelő
  //xml fájlja.
  private static String[][] types = {
    {"Eclipse", "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml"},
    {"Default-alt", "/org/fife/ui/rsyntaxtextarea/themes/default-alt.xml"},
    {"Default", "/org/fife/ui/rsyntaxtextarea/themes/default.xml"},
    {"Dark", "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"},
    {"Idea", "/org/fife/ui/rsyntaxtextarea/themes/idea.xml"},
    {"Monokai", "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"},
    {"Visual Studio", "/org/fife/ui/rsyntaxtextarea/themes/vs.xml"},
  };
/******************************************************************************/
  //Ezzel vagyunk képesek megváltoztatni a színezés típusát.
  public static void createChangeSyntaxStyle(JMenu jmenu, RSyntaxTextArea rsta) {
    //Valahogy jelölni kell, hogy éppen melyik van használatban.
    JRadioButtonMenuItem rbm[] = new JRadioButtonMenuItem[types.length];

    //Létrehozzuk a szükséges menüelemeket.
    ButtonGroup bg = new ButtonGroup();
    JMenu tmp = new JMenu("Change Syntax Style");

    //Végig iteráljuk a típusokat, hogy minden egyes típus és beállítjuk
    //rájuk a listenerket is.
    for(int i = 0; i < types.length; i++) {
      rbm[i] = new JRadioButtonMenuItem(types[i][0]);
      tmp.add(rbm[i]);
      bg.add(rbm[i]);
      rbm[i].addActionListener(new ChangeSyntaxStyleListener(types[i][1], rsta));
    }

    //Alapjáraton az első teszi meg kiválasztottnak.
    rbm[0].setSelected(true);
    jmenu.add(tmp);
  }
}
/******************************************************************************/
//Itt valósítjuk meg a listenert.
class ChangeSyntaxStyleListener implements ActionListener {
  //Tudnunk kell, hogy milyen típusról van szó és azt, hogy minek kell
  //beállítani ezeket.
  private String type;
  private RSyntaxTextArea rs;
/******************************************************************************/
  //Konstruktor.
  public ChangeSyntaxStyleListener(String th, RSyntaxTextArea rsta) {
    this.rs = rsta;
    type = new String(th);
  }
/******************************************************************************/
  //A figyelő.
  public void actionPerformed(ActionEvent ev) {
    try {
      //Beállítjuk a témát és elfogadtatjuk a kapott komponenssel.
      Theme theme = Theme.load(getClass().getResourceAsStream(type));
      theme.apply(rs);
    } catch (Exception e){
      System.out.println(e);
    }
  }
/******************************************************************************/
}
