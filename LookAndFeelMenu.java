package nor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/******************************************************************************/

public class LookAndFeelMenu {

  //Megváltoztatja a témát.
  public static void createLookAndFeelMenuItem(JMenu jmenu, Component cmp) {
    //Megnézzük milyen témák vannak fent a gépen.
    final UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();

    //Létrehozzuk a szükséges elemeket.
    JRadioButtonMenuItem rbm[] = new JRadioButtonMenuItem[infos.length];
    ButtonGroup bg = new ButtonGroup();
    JMenu tmp = new JMenu("Change Look and Feel");
    tmp.setMnemonic('C');

    //Majd a témákat végig iterálva elmentjük ezeket és még beállítjuk
    //a listenereket is minden menüpotra.
    for(int i = 0; i < infos.length; i++) {
      rbm[i] = new JRadioButtonMenuItem(infos[i].getName());
      rbm[i].setMnemonic(infos[i].getName().charAt(0));
      tmp.add(rbm[i]);
      bg.add(rbm[i]);
      rbm[i].addActionListener(new LookAndFeelMenuListener(infos[i].getClassName(), cmp));
  }

  rbm[0].setSelected(true);
  jmenu.add(tmp);
  }
}
/******************************************************************************/
class LookAndFeelMenuListener implements ActionListener {
  //Kell, hogy milyen témát választott és, hogy milyen componenshez akarja
  //hozzá adni.
  String classname;
  Component jf;
/******************************************************************************/
  //Konstruktor.
  public LookAndFeelMenuListener(String cln, Component jf) {
    this.jf = jf;
    classname = new String(cln);
  }
/******************************************************************************/
  //Figyelő.
  public void actionPerformed(ActionEvent ev) {
    try {
      UIManager.setLookAndFeel(classname);
      SwingUtilities.updateComponentTreeUI(jf);
    } catch (Exception e){
      System.out.println(e);
    }
  }
/******************************************************************************/
}
