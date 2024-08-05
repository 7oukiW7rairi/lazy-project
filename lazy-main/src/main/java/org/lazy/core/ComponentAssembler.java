package org.lazy.core;

import java.util.*;

public interface ComponentAssembler<T> {

    Object assembleComponent(T assembleBase, Map<String, Object> dependencies) throws CoreException;

    boolean canAssemble(T assembleBase);

}
