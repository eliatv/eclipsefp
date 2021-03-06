// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.core.builder;

import java.util.Map;
import net.sf.eclipsefp.haskell.core.HaskellCorePlugin;
import net.sf.eclipsefp.haskell.core.internal.util.CoreTexts;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

/** <p>The incremental builder for Haskell projects.</p>
  *
  * @author Leif Frenzel
  */
public class HaskellBuilder extends IncrementalProjectBuilder {

  public static final String BUILDER_ID = HaskellBuilder.class.getName();

  @Override
  protected IProject[] build( final int kind,
                              final Map args,
                              final IProgressMonitor monitor )
                                                          throws CoreException {
    checkOutFolders( new SubProgressMonitor( monitor, 5 ) );
    performBuild( kind, new SubProgressMonitor( monitor, 95 ) );
    scheduleRefresh();
    return null;
  }


  // helping methods
  //////////////////

  private void checkOutFolders( final SubProgressMonitor monitor ) {
    IWorkspaceRunnable op = new CheckOutFoldersOperation( getProject() );
    try {
      ResourcesPlugin.getWorkspace().run( op, monitor );
    } catch( CoreException cex ) {
      String msg = "Problem while checking out and bin folder existence."; //$NON-NLS-1$
      HaskellCorePlugin.log( msg, cex );
    }
  }

  private void performBuild( final int kind,
                             final IProgressMonitor mon ) throws CoreException {
    if( kind == IncrementalProjectBuilder.FULL_BUILD ) {
      fullBuild( mon );
    } else {
      IResourceDelta delta = getDelta( getProject() );
      if( delta == null ) {
        fullBuild( mon );
      } else {
        incrementalBuild( delta, mon );
      }
    }
  }


  private void scheduleRefresh() {
    Job job = new Job( CoreTexts.haskellBuilder_refreshing ) {
      @Override
      public IStatus run( final IProgressMonitor monitor ) {
        IStatus result = Status.OK_STATUS;
        try {
          getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        } catch( CoreException cex ) {
          String msg = "Problem during resource refresh after build."; //$NON-NLS-1$
          HaskellCorePlugin.log( msg, cex );
          result = cex.getStatus();
        }
        return result;
      }
    };
    job.schedule();
  }

  private void fullBuild( final IProgressMonitor mon ) throws CoreException {
    mon.beginTask( CoreTexts.haskellBuilder_full, 100 );
    try {
      IWorkspaceRunnable op = new CleanOutFoldersOperation( getProject() );
      ResourcesPlugin.getWorkspace().run( op,
                                          new SubProgressMonitor( mon, 15 ) );

      mon.subTask( CoreTexts.haskellBuilder_compiling );
      SubProgressMonitor subMon = new SubProgressMonitor( mon, 85 );
      getProject().accept( new BuildVisitor( subMon ) );
    } finally {
      mon.done();
    }
  }

  private void incrementalBuild( final IResourceDelta delta,
                                 final IProgressMonitor mon )
                                                          throws CoreException {
    delta.accept( new DeltaBuildVisitor( mon ) );
  }
}