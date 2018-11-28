package nor;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.io.*;

public class NornsTest {

  private Norns nor;


  @Before
  public void setUp() throws Exception {
    nor = new Norns();
  }

  // 1. Norns
  @Test
  public void testCreateMenuBar(){
    nor.createMenuBar(nor.frame);
    assertNotNull(nor.frame.getJMenuBar());
  }

  // 2. Norns
  @Test
  public void testCreateCompletionProvider(){
    CompletionProvider provider = nor.createCompletionProvider();
    assertNotNull(provider);
  }

  // 3. Norns
  public void testCreateMenu(){
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = nor.createMenu("Valami", KeyEvent.VK_V, menuBar);
    assertNotNull(menu);
  }

  // 4. Norns
  public void testSetSyntax(){
    nor.setSyntax("java");
    assertEquals("SYNTAX_STYLE_JAVA", nor.textArea.getSyntaxEditingStyle());
  }

  // 5. FileOperation
  public void testIsSave(){
    nor.fileHandler.setSave(true);
    assertEquals(true, nor.fileHandler.isSave());
  }

  // 6. FileOperation
  public void testSetSave(){
    nor.fileHandler.setSave(false);
    boolean b = nor.fileHandler.isSave();
    assertEquals(false, b);
  }

  // 7. FileOperation
  public void testGetFileName(){
    nor.fileHandler.setFileName("valami");
    assertEquals("valami", nor.fileHandler.getFileName());
  }

  // 8. FileOperation
  public void testSetFileName(){
    nor.fileHandler.setFileName("valami");
    String str = nor.fileHandler.getFileName();
    assertEquals("valami", str);
  }

  // 9. FileOperation
  public void testConfirmSave(){
    nor.fileHandler.setSave(true);
    assertEquals(true, nor.fileHandler.confirmSave());
  }

  // 10. FontChooser
  public void tesCreateFont(){
    FontChooser dia = null;
    Font fnt = dia.createFont();
    assertNotNull(fnt);
  }
}
