// Copyright (c) 2004-2005 by Leif Frenzel
// See http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.internal.views.common;

import net.sf.eclipsefp.haskell.core.halamo.IModule;

/** <p>grouping object that is used on viewers to group import statements.
  * It has it's own visual representation, import statements appear below
  * it.</p>
  *
  * @author Leif Frenzel
  */
public class ImportGroup {

  private final IModule module;

  public ImportGroup( final IModule module ) {
    this.module = module;
  }

  public IModule getModule() {
    return module;
  }
}
