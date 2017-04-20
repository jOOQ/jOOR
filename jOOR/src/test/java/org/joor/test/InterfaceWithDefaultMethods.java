package org.joor.test;

/**
 * @author Thomas Darimont
 */
public interface InterfaceWithDefaultMethods {

  default int returnAnInt() {
    return 42;
  }

  default int throwIllegalArgumentException(){
    throw new IllegalArgumentException("oh oh");
  }
}
