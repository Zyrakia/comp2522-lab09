package ca.bcit.comp2522.lab09;

/**
 * Represents a class that requires cleaning up when "destroyed".
 *
 * @author Ole Lammers
 * @version 1.0
 */
public interface Destroyable {

    /**
     * Performs any cleanup actions required by this class.
     */
    public void destroy();
}
