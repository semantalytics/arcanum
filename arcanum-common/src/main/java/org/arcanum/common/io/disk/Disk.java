package org.arcanum.common.io.disk;

/**
 * @author Angelo De Caro (arcanumlib@gmail.com)
 * @since 1.0.0
 */
public interface Disk<S extends Sector> {

    S getSectorAt(int index);

    S getSector(String key);

    void flush();
}
