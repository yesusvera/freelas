package br.com.ibracon.idr.form;

public class Flag {

    private boolean isSet;

    public synchronized void set() {
        isSet = true;
        notifyAll();
    }

    public synchronized void clear() {
        isSet = false;
    }

    public synchronized void waitForFlag() {
        if (!isSet) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }
    }

    public synchronized void interruptibleWaitForFlag()
            throws InterruptedException {
        if (!isSet) {
            wait();
        }
    }
}
