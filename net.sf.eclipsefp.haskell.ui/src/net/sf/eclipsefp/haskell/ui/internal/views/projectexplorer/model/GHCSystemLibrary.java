// Copyright (c) 2006-2008 by Leif Frenzel - see http://leiffrenzel.de
// This code is made available under the terms of the Eclipse Public License,
// version 1.0 (EPL). See http://www.eclipse.org/legal/epl-v10.html
package net.sf.eclipsefp.haskell.ui.internal.views.projectexplorer.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.eclipsefp.haskell.core.compiler.CompilerManager;
import net.sf.eclipsefp.haskell.core.compiler.ICompilerManager;
import net.sf.eclipsefp.haskell.core.internal.hsimpl.IHsImplementation;
import net.sf.eclipsefp.haskell.core.util.GHCSyntax;
import net.sf.eclipsefp.haskell.core.util.QueryUtil;
import net.sf.eclipsefp.haskell.ui.internal.views.common.ITreeElement;
import net.sf.eclipsefp.haskell.ui.util.IImageNames;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class GHCSystemLibrary implements ITreeElement {

  private final IProject project;

  public GHCSystemLibrary( final IProject project ) {
    this.project = project;
  }


  // interface methods of ITreeElement
  ////////////////////////////////////

  public List<GHCPackageConf> getChildren() {
    List<GHCPackageConf> result = new ArrayList<GHCPackageConf>();
    ICompilerManager man = CompilerManager.getInstance();
    if( man.getCurrentHsImplementation() != null ) {
      IPath binDir = new Path( man.getCurrentHsImplementation().getBinDir() );
      String exe =  binDir.append( GHCSyntax.GHC_PKG ).toOSString();
      String queryResult = QueryUtil.query( exe, "list" );
      parsePackageList( queryResult, result );
    }
    return result;
  }

  public Object getParent() {
    return project;
  }

  public String getText() {
    ICompilerManager man = CompilerManager.getInstance();
    IHsImplementation impl = man.getCurrentHsImplementation();
    String name = "No Haskell implementation configured!";
    if( impl != null ) {
      name = impl.getName();
    }
    return "GHC Libraries [" + name + "]";
  }

  public String getImageKey() {
    return IImageNames.IMPORT_LIBRARY;
  }


  // helping functions
  ////////////////////

  private void parsePackageList( final String content,
                                 final List<GHCPackageConf> confs ) {
    Map<String, StringBuilder> entries
      = new HashMap<String,StringBuilder>();
    try {
      BufferedReader br = new BufferedReader( new StringReader( content ) );
      StringBuilder sb = null;
      String line = br.readLine();
      while( line != null ) {
        if( line.startsWith( "  " ) && sb != null ) {
          sb.append(line.trim());
        } else {
          sb = new StringBuilder();
          String withoutColon = line.substring(0, line.length() - 1);
          entries.put(withoutColon, sb);
        }
        line = br.readLine();
      }
    } catch( final IOException ioex ) {
      // won't happen, we're just reading a string
    }
    for( String entry: entries.keySet() ) {
      Path loc = new Path( entry.trim() );
      confs.add( new GHCPackageConf( this, loc, entries.get(entry).toString() ) );
    }
  }
}
