// code by jph
package ch.alpine.owl.math.region;

import java.util.HashSet;
import java.util.Set;

import ch.alpine.java.lang.ClassDiscovery;
import ch.alpine.java.lang.ClassPaths;
import ch.alpine.java.lang.ClassVisitor;
import junit.framework.TestCase;

public class RegionTest extends TestCase {
  public void testSimple() {
    Set<Class<?>> regions = new HashSet<>();
    ClassVisitor classVisitor = new ClassVisitor() {
      @Override
      public void classFound(String jarfile, Class<?> cls) {
        if (Region.class.isAssignableFrom(cls))
          regions.add(cls);
      }
    };
    ClassDiscovery.execute(ClassPaths.getDefault(), classVisitor);
    assertTrue(60 < regions.size());
  }
}
