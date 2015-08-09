// $Id: KeyStoreAliasList.java 19740 2014-07-22 13:39:16Z mike $

package org.faceless.pdf2.viewer3.util;

import java.awt.*;
import javax.swing.*;
import java.beans.*;
import java.util.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import org.faceless.pdf2.viewer3.Util;
import org.faceless.pdf2.viewer3.KeyStoreManager;
import org.faceless.pdf2.FormSignature;
import org.faceless.util.CombinedKeyStore;
import org.faceless.util.SortedListModel;

/**
 * A JList that displays a list of aliases from a KeyStore managed by a KeyStoreManager
 */
public class KeyStoreAliasList extends JList<String> implements Comparator<String>, ListCellRenderer<String>, PropertyChangeListener {
    private KeyStoreManager ksm;
    private SortedListModel<String> model;
    private final boolean keys, certificates;
    private static final Color TRANSPARENT = new Color(0, true);

    public KeyStoreAliasList(boolean keys, boolean certificates) {
        this.keys = keys;
        this.certificates = certificates;
        this.model = new SortedListModel<String>(this);
        this.setVisibleRowCount(6);
        setModel(model);
//        addPropertyChangeListener(this);
        setCellRenderer(this);
    }

    public KeyStoreAliasList(KeyStoreManager ksm, boolean keys, boolean certificates) {
        this(keys, certificates);
        setKeyStoreManager(ksm);
    }

    public KeyStoreManager getKeyStoreManager() {
        return ksm;
    }

    public void setKeyStoreManager(KeyStoreManager ksm) {
        try {
            if (this.ksm != null) {
                this.ksm.removePropertyChangeListener(this);
                model.clear();
            }
            this.ksm = ksm;
            KeyStore keystore;
            try {
                keystore = ksm.getKeyStore();
            } catch (Exception e) {
                keystore = null;
            }
            if (keystore != null) {
                for (Enumeration<String> e = keystore.aliases();e.hasMoreElements();) {
                    String alias = e.nextElement();
                    if (isDisplayed(keystore, alias)) {
                        model.add(alias);
                    }
                }
                ksm.addPropertyChangeListener(this);
            }
        } catch (RuntimeException e)  {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("alias")) {
            try {
                if (event.getOldValue() != null) {
                    model.remove(event.getOldValue());
                }
                if (event.getNewValue() != null && isDisplayed(ksm.getKeyStore(), (String)event.getNewValue())) {
                    model.add((String)event.getNewValue());
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isDisplayed(KeyStore keystore, String alias) {
        try {
            if (keys && keystore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
                return true;
            } else if (!keys && keystore.entryInstanceOf(alias, KeyStore.TrustedCertificateEntry.class)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled(KeyStore keystore, String alias) {
        return true;
    }

    public boolean isReadOnly(KeyStore keystore, String alias) {
        if (keystore instanceof CombinedKeyStore) {
            CombinedKeyStore cks = (CombinedKeyStore) keystore;
            return cks.isReadOnly(alias);
        }
        return false;
    }

    public int compare(String o1, String o2) {
        int diff = 0;
        try {
            KeyStore keystore = ksm.getKeyStore();
            if (o1 == null ? o2 != null : !o1.equals(o2)) {
                X509Certificate c1 = (X509Certificate)keystore.getCertificate(o1);
                if (c1 == null) {
                    Certificate[] chain = keystore.getCertificateChain(o1);
                    if (chain != null) {
                        c1 = (X509Certificate)chain[0];
                    }
                }
                X509Certificate c2 = (X509Certificate)keystore.getCertificate(o2);
                if (c2 == null) {
                    Certificate[] chain = keystore.getCertificateChain(o2);
                    if (chain != null) {
                        c2 = (X509Certificate)chain[0];
                    }
                }
                if (c1 == null && c2 == null) {
                    return 0;
                } else if (c1 == null) {
                    return 1;
                } else if (c2 == null) {
                    return -1;
                } else {
                    String n1 = c1.getSubjectX500Principal().toString();
                    String n2 = c2.getSubjectX500Principal().toString();
                    diff = n1.compareTo(n2);
                    if (diff == 0) {
                        diff = c1.getNotAfter().compareTo(c2.getNotAfter());
                    }
                    if (diff == 0) {
                        diff = c1.getSerialNumber().compareTo(c2.getSerialNumber());
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            diff = o1.hashCode() - o2.hashCode();
        }
        return diff;
    }

    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean hasFocus) {
        try {
            KeyStore keystore = ksm.getKeyStore();
            String alias = (String)value;
            boolean enabled = isEnabled(keystore, alias);
            isSelected &= enabled;
            X509Certificate cert = (X509Certificate)keystore.getCertificate(alias);
            if (cert == null) {
                Certificate[] certs = keystore.getCertificateChain(alias);
                if (certs != null) {
                    cert = (X509Certificate)certs[0];
                }
            }
            String key = cert == null ? alias : FormSignature.getSubjectField(cert, "CN");
            if (key == null) {
                key = FormSignature.getSubjectField(cert, "O");
            }
            JLabel label = new JLabel(key);
            boolean labelEnabled = enabled && !isReadOnly(keystore, alias);
            label.setEnabled(labelEnabled);
            try {
                if (cert != null) {
                    cert.checkValidity();
                }
                label.setIcon(new ImageIcon(KeyStoreManager.class.getResource("resources/icons/accept.png")));
            } catch (Exception e) {
                label.setIcon(new ImageIcon(KeyStoreManager.class.getResource("resources/icons/error.png")));
                label.setToolTipText(UIManager.getString("PDFViewer.InvalidWhy").replaceAll("\\{1\\}", (e.getMessage() == null ? e.toString() : e.getMessage())));
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label);
            panel.setOpaque(true);
            panel.setBackground(isSelected ? list.getSelectionBackground() : TRANSPARENT);
            panel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            panel.setFont(list.getFont());
            panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            return panel;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

