package edu.uprm.ece.icom4015.jabrisca.client.views;

import edu.uprm.ece.icom4015.jabrisca.client.JabriscaController;

/**
 *
 * @author EltonJohn
 */
public class EndGameWindow extends JabriscaJPanel {

	/**
     * Creates new form EndGameWindow
     */
    public EndGameWindow() {
        super();
    }
    
    /**
     * Creates new form EndGameWindow
     */
    public EndGameWindow(JabriscaController listener) {
        super(listener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        jPanel1.setPreferredSize(new java.awt.Dimension(640, 30));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jProgressBar1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jProgressBar1.setMinimumSize(new java.awt.Dimension(10, 10));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(146, 10));
        jPanel1.add(jProgressBar1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("status");
        jLabel1.setName("statusBar_status");
        jProgressBar1.setName("statusBar_progressBar");
        jPanel1.add(jLabel1, java.awt.BorderLayout.LINE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setMaximumSize(new java.awt.Dimension(118, 249));
        jPanel2.setPreferredSize(new java.awt.Dimension(640, 249));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("display"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jPanel2.add(jScrollPane1);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jButton1.setText("Continue");
        jButton1.setName("continue"); // NOI18N
        jPanel3.add(jButton1);

        jButton3.setLabel("Surrender");
        jButton3.setName("surrender"); // NOI18N
        jPanel3.add(jButton3);

        jPanel2.add(jPanel3);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Options");
        jMenu1.setName("howToPlay"); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("How to Play Brisca");
        jMenuItem1.setName("howToPlay"); // NOI18N
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);
        jTextArea1.setEditable(false);
        pack();
        setResizable(false);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EndGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EndGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EndGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EndGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EndGameWindow(new JabriscaController()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}