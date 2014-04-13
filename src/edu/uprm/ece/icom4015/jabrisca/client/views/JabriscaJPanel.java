package edu.uprm.ece.icom4015.jabrisca.client.views;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

import edu.uprm.ece.icom4015.jabrisca.client.JabriscaController;

public abstract class JabriscaJPanel extends JFrame{
	JabriscaController listener;
	
	public JabriscaJPanel() {
		initComponents();
		addSettingsMenu();
	}

	/**
	 * 
	 */
	private void addSettingsMenu() {
		JMenu jMenu= new JMenu();
		jMenu.setText("Program Settings");
        jMenu.setName("settings");
        
		JMenuItem jMenuItem = new JMenuItem();
		jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem.setText("Reconnect");
        jMenuItem.setName("reconnect"); // NOI18N
        jMenu.add(jMenuItem);
        
        getJMenuBar().add(jMenu);
	}
	
	/**
	 * Initialize listener
	 * @param listener
	 */
	public JabriscaJPanel(JabriscaController listener) {
		this();
		setJabriscaController(listener);
		this.setName(this.getClass().getName());
		this.addWindowListener(listener);
	}
	
	abstract void initComponents();
	
	/**
	 * Fetches the component and returns it to. Has default access or package
	 * access modifier
	 * 
	 * @param name
	 * @return null if component is not found, otherwise returns the first
	 *         component with the given name
	 */
	public Component fetchComponent(Container container, String name) {
		Container currentPanel = (Container) (container == null ? this.getContentPane()
				: container); // for recursive call
		for (int i = 0; i < currentPanel.getComponentCount(); i++) {
			Component component = currentPanel.getComponent(i);
			if (component instanceof JPanel || component instanceof JScrollPane
					|| component instanceof JViewport) {
				// do forced checking for the container
				Container container1 = (Container) component;
				component = fetchComponent((Container) component, name);
				if (component != null) {
					return component; // if I found the component nested inside
										// one of the containers return it
				} else if (container1.getName() != null
						&& container1.getName().equals(name)) {
					return container; // the container itself is the object
				}
			} else if (component.getName() != null
					&& component.getName().equals(name)) {
				// base case
				return currentPanel.getComponent(i);
			}
		}
		return null;
	}
	
	/**
	 * add the listener to all other subclasses and menu
	 * @param listener
	 * @param container defaults to the content pane
	 */
	private void setJabriscaController(JabriscaController listener){
		this.listener = listener;
		setJabriscaControllerBody(listener,null);
		
		//Set the listener to the menu components
		JMenuBar menuBar = this.getJMenuBar() ;
		for(int i=0;i<menuBar.getComponentCount();i++){
			JMenu comp = (JMenu) menuBar.getComponent(i);
			setJabriscaControllerMenu(listener,comp);
		}
	}
	
	/**
	 * Add the controller to the menu if this frame has any any
	 * @param listener2
	 * @param container
	 */
	private void setJabriscaControllerMenu(JabriscaController listener2,
			JMenu menu) {
			for(int i=0;i<menu.getItemCount();i++){
				JMenuItem comp = menu.getItem(i);
				if(comp instanceof JMenu){
					setJabriscaControllerMenu(listener2,(JMenu) comp);
				}else{
					comp.addActionListener(listener2);
				}
			}
	}
	

	/**
	 * add the listener to all other subclasses
	 * @param listener
	 */
	private void setJabriscaControllerBody(JabriscaController listener,Container container){
		container = container == null ?this.getContentPane():container; // for recursive call
		for(int i=0;i<container.getComponentCount();i++){
			Component comp = container.getComponent(i);
			if(comp instanceof JButton){
				JButton butt = (JButton)comp;
				butt.addActionListener(listener);
				butt.addKeyListener(listener);
			} else if(comp instanceof JTextField){
				JTextField field = (JTextField)comp;
				field.addActionListener(listener);
				field.addKeyListener(listener);
			} else if(comp instanceof JComboBox){
				JComboBox field = (JComboBox)comp;
				field.addActionListener(listener);
				field.addKeyListener(listener);
			}else if(comp instanceof JTextArea){
				JTextArea text = (JTextArea) comp;
				text.addMouseListener(listener);
				text.addKeyListener(listener);
				text.addKeyListener(listener);
			} else if(comp instanceof JLabel){
				JLabel text = (JLabel) comp;
				text.addMouseListener(listener);
				text.addKeyListener(listener);
			}else if(comp instanceof JTable){
				JTable text = (JTable) comp;
				text.addMouseListener(listener);
				text.addKeyListener(listener);
			}else if(comp instanceof JRadioButton){
				JRadioButton mItem = (JRadioButton) comp;
				mItem.addActionListener(listener);
				mItem.addKeyListener(listener);
			}else if(comp instanceof Container){
				//recursive call
				setJabriscaControllerBody(listener,(Container)comp); // add listeners to all subcomponents
			} 
		}
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void addJabriscaController(JabriscaController listener) {
	 	setJabriscaController(listener);
	}
	
	/**
	 * Tries to fetch the "Text" from a JTextArea,JTextField
	 * @param componentName
	 * @return
	 */
	public String fetchJTextValue(String componentName) {
		String result =null;
		Component comp = fetchComponent(null, componentName);
		if(comp instanceof JTextField){
			result = ((JTextField) comp).getText();
		} else if(comp instanceof JTextArea){
			result = ((JTextArea) comp).getText();
		}
		return result;
	}
	
	/**
	 * Tries to fetch if the JRadioButton is selected
	 * @param componentName
	 * @return null if button was not found, "true" if is select 
	 */
	public String fetchJRadioButtonValue(String componentName) {
		String result = null;
		Component comp = fetchComponent(null, componentName);
		if(comp instanceof JRadioButton){
			result = ((JRadioButton) comp).isSelected()+"";
		}
		return result;
	}

	public void setStatus(String string) {
		JLabel  statusLabel =  (JLabel)fetchComponent(null, "statusBar_status");
		statusLabel.setText(string);
	}

	public void setProgressBar(int i) {
		JProgressBar progressBar = (JProgressBar)fetchComponent(null, "statusBar_progressBar");
		progressBar.setMaximum(100);;
		progressBar.setValue(i);
		progressBar.setStringPainted(true);
	}

	public void fetchComponentAndAddValueJTextArea(Container object,
			String name, String message) {
		JTextArea display = (JTextArea)(fetchComponent(object, name));
		display.setText(display.getText() + message);
	}
	
	
}
