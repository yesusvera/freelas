// $Id: PromptingAuthenticator.java 19665 2014-07-16 08:47:01Z mike $

package org.faceless.pdf2.viewer3.util;

import org.faceless.pdf2.viewer3.Util;
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.*;

/**
 * A simple {@link Authenticator} which will prompt the user to enter
 * the required name and password with a Swing dialog. Names and passwords
 * are cached by default, but this behaviour can be disabled by calling {@link #setCaching}.
 * To use, simply call <code>Authenticator.setDefault(new PromptingAuthenticator(component))</code>
 * @since 2.14.1
 */
public class PromptingAuthenticator extends Authenticator {

    private Component root;
    private Map<String,PasswordAuthentication> cache;

    /**
     * Create a new PromptingAuthenticator with no root component specified
     */
    public PromptingAuthenticator() {
       setCaching(true);
    }

    /**
     * Create a new PromptingAuthenticator and specify the Component the dialog should be opened above
     * @param root the root component for the dialog
     */
    public PromptingAuthenticator(Component root) {
       setRoot(root);
       setCaching(true);
    }

    /**
     * Set the root Component the dialog should be opened above
     * @param root the root component for the dialog
     */
    public void setRoot(Component root) {
        this.root = root;
    }

    /**
     * Set whether to cache names/passwords - if true (the default), the dialog will
     * be presented with the previous values already specified.
     * @param cache whether to cache names/password
     */
    public void setCaching(boolean cache) {
        if (cache && this.cache == null) {
            this.cache = new HashMap<String,PasswordAuthentication>();
        } else if (!cache && this.cache != null) {
            this.cache = null;
        }
    }

    protected PasswordAuthentication getPasswordAuthentication() {
         if (SwingUtilities.isEventDispatchThread()) {
             PasswordAuthentication auth = null;
             if (cache != null) {
                 auth = cache.get(getRequestingPrompt());
             }
             DialogPanel dialog = new DialogPanel();
             JPanel panel = new JPanel(new GridBagLayout());
             final Insets myinsets = new Insets(4, 8, 4, 8);
             JTextField name;
             JPasswordField password;
             final String title = Util.getUIString("PDFViewer.AuthenticationRequired", getRequestingPrompt());

             panel.add(new JLabel(title), new GridBagConstraints() {{ anchor=CENTER; gridwidth=REMAINDER; insets=myinsets; }});
             panel.add(new JLabel(UIManager.getString("PDFViewer.Name")), new GridBagConstraints() {{ anchor=WEST; insets=myinsets; }});
             panel.add(name=new JTextField(20), new GridBagConstraints() {{ anchor=WEST; fill=BOTH; weightx=1; gridwidth=REMAINDER; insets=myinsets; }});
             panel.add(new JLabel(UIManager.getString("PDFViewer.Password")), new GridBagConstraints() {{ anchor=WEST; insets=myinsets; }});
             panel.add(password=new JPasswordField(), new GridBagConstraints() {{ anchor=WEST; fill=BOTH; weightx=1; gridwidth=REMAINDER; insets=myinsets; }});
             dialog.addComponent(panel);

             if (auth != null) {
                 name.setText(auth.getUserName());
                 password.setText(new String(auth.getPassword()));
             }

             if (dialog.showDialog(root, title)) {
                 auth = new PasswordAuthentication(name.getText(), password.getPassword());
                 if (cache != null) {
                     cache.put(getRequestingPrompt(), auth);
                 }
             } else {
                 auth = null;
                 if (cache != null) {
                     cache.remove(getRequestingPrompt());
                 }
             }
             return auth;
         } else {
             final PasswordAuthentication[] auth = new PasswordAuthentication[1];
             try {
                 SwingUtilities.invokeAndWait(new Runnable() {
                     public void run() {
                         auth[0] = getPasswordAuthentication();
                     }
                 });
             } catch (Exception e) { 
                 // Ignore
             }
             return auth[0];
         }
     }

}
